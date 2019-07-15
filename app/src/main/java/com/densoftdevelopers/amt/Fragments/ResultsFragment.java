package com.densoftdevelopers.amt.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.densoftdevelopers.amt.R;
import com.densoftdevelopers.amt.RequestHandler;
import com.densoftdevelopers.amt.SharedPrefmanager;
import com.densoftdevelopers.amt.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.densoftdevelopers.amt.Fragments.ScanFragment.device_id;
import static com.densoftdevelopers.amt.Fragments.ScanFragment.sim_serial;
import static com.densoftdevelopers.amt.HomeActivity.REPORT_SEND_URL;
import static com.densoftdevelopers.amt.HomeActivity.RESULTS_FETCH;

public class ResultsFragment extends Fragment {

    TextView attempts_value, attempts_Number, imsi_Value, imsi_State,device_id_Value, device_id_State,
            voltage_Value, voltage_State, callibartion_Value, callibatation_State,date_time_text,date_time_Value;
    Button sendReport, scanAnotherDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attempts_value = (TextView) getView().findViewById(R.id.attempts_txt_view);
        attempts_Number = (TextView) getView().findViewById(R.id.attempts_number);
        imsi_Value = (TextView) getView().findViewById(R.id.imsi_value);
        imsi_State = (TextView) getView().findViewById(R.id.imsi_state);
        device_id_Value = (TextView) getView().findViewById(R.id.device_id_value);
        device_id_State = (TextView) getView().findViewById(R.id.device_id_state);
        voltage_Value = (TextView) getView().findViewById(R.id.voltage_value);
        voltage_State = (TextView) getView().findViewById(R.id.voltage_state);
        callibartion_Value = (TextView) getView().findViewById(R.id.calibration_value);
        callibatation_State = (TextView) getView().findViewById(R.id.calibration_state);
        date_time_text = (TextView) getView().findViewById(R.id.date_time_text);
        date_time_Value = (TextView) getView().findViewById(R.id.date_time_value);

        User user = SharedPrefmanager.getInstance(getActivity()).getUser();
        attempts_Number.setText(user.getAttempts());

        populateFields();


        scanAnotherDevice = (Button) getView().findViewById(R.id.scan_another_device);
        scanAnotherDevice.setVisibility(View.GONE);

        scanAnotherDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction scan = getFragmentManager().beginTransaction();
                Fragment mfrag = new ScanFragment();
                scan.replace(R.id.fragment_container,mfrag);
                scan.commit();
            }
        });

        sendReport = (Button) getView().findViewById(R.id.send_report_btn);
        sendReport.setVisibility(View.GONE);
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendReport();
            }
        });


    }

    private void SendReport() {
        class SendReportToAdmin extends AsyncTask<Void, Void, String>
        {

            ProgressDialog waitingDialog = new ProgressDialog(getActivity());

            public ProgressDialog getWaitingDialog() {
                return waitingDialog;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                waitingDialog.setTitle("send report to admin");
                waitingDialog.setMessage("please wait as we send report to admin");
                waitingDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                User user = SharedPrefmanager.getInstance(getActivity()).getUser();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("name",user.getName());
                params.put("email",user.getEmail());
                params.put("qr_number",device_id_Value.getText().toString());
                params.put("sim_serial",imsi_Value.getText().toString());

                //creating request parameters

                params.put("datetime",date_time_text.getText().toString());

                params.put("attempts",String.valueOf(user.getAttempts()));

                if (imsi_Value.getText().toString().equals("FAIL") || device_id_Value.getText().toString().equals("FAIL") || voltage_State.getText().toString().equals("FAIL") || callibatation_State.getText().toString().equals("FAIL"))
                {
                    params.put("test_result","fail");
                }else
                {
                    params.put("test_result","pass");
                }

                return requestHandler.sendPostRequest(REPORT_SEND_URL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try{
                    //converting response to json
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error"))
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        sendReport.setVisibility(View.GONE);
                        scanAnotherDevice.setVisibility(View.VISIBLE);
                        emptyFields();
                    }
                    else
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        sendReport.setText("TRY AGAIN");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    waitingDialog.dismiss();
                    sendReport.setText("TRY AGAIN");
                    Toast.makeText(getActivity(), "could not connect", Toast.LENGTH_SHORT).show();
                }
            }
        }

        SendReportToAdmin sendReportToAdmin = new SendReportToAdmin();
        sendReportToAdmin.execute();

    }

    private void emptyFields() {

        attempts_Number.setText("");
        attempts_value.setText("Attempts:");
        imsi_State.setText("");
        imsi_Value.setText("Imsi:");
        device_id_State.setText("");
        device_id_Value.setText("Device Id:");
        voltage_State.setText("");
        voltage_Value.setText("Voltage:");
        callibatation_State.setText("");
        callibartion_Value.setText("Calibration");
        date_time_Value.setText("");
    }

    private void populateFields() {

        class Results  extends AsyncTask<Void, Void , String>
        {

            ProgressDialog waitingDialog = new ProgressDialog(getActivity());

            public ProgressDialog getWaitingDialog() {
                return waitingDialog;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                waitingDialog.setTitle("Generating Report");
                waitingDialog.setMessage("Please wait as we generate the report");
                waitingDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //creating a request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String ,String> params = new HashMap<>();
                params.put("qr_number",device_id.getText().toString());
                params.put("serial_num",sim_serial.getText().toString());

                //return the response
                return requestHandler.sendPostRequest(RESULTS_FETCH,params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try{
                    //converting response to json
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error"))
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        imsi_Value.setText("imsi: "+obj.getString("imsi"));
                        imsi_State.setText("pass");
                        device_id_Value.setText("device id: "+obj.getString("qr_number"));
                        device_id_State.setText("pass");
                        voltage_Value.setText("voltage: "+obj.getString("voltage"));
                        String voltage = obj.getString("voltage");
                        float volt_limit = Float.parseFloat(voltage);
                        if (volt_limit < 4.7)
                        {
                            voltage_State.setText("fail");
                        }
                        else
                        {
                            voltage_State.setText("pass");
                        }
                        callibartion_Value.setText("callibration: "+obj.getString("callibration"));
                        String callibration = obj.getString("callibration");
                        if (callibration.equals("f"))
                        {
                            callibatation_State.setText("fail");
                        }
                        else
                        {
                            callibatation_State.setText("pass");
                        }
                        date_time_text.setText("DateTime");
                        date_time_Value.setText(obj.getString("datetime"));

                        sendReport.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    waitingDialog.dismiss();
                    Toast.makeText(getActivity(), "failed to connect", Toast.LENGTH_SHORT).show();
                }
            }
        }

        Results results = new Results();
        results.execute();
    }
}
