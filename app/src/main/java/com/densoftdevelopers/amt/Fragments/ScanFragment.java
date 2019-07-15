package com.densoftdevelopers.amt.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.densoftdevelopers.amt.Common.Common;
import com.densoftdevelopers.amt.Device_Id;
import com.densoftdevelopers.amt.HomeActivity;
import com.densoftdevelopers.amt.R;
import com.densoftdevelopers.amt.RequestHandler;
import com.densoftdevelopers.amt.SharedPrefmanager;
import com.densoftdevelopers.amt.Sim_Serial;
import com.densoftdevelopers.amt.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.densoftdevelopers.amt.HomeActivity.ATTEMPTS_UPDATE_URL;
import static com.densoftdevelopers.amt.HomeActivity.VALIDATING_QR_IMSI_URL;


public class ScanFragment extends Fragment {

   public static TextView device_id, sim_serial;
   private Button  validate;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan, container, false);

        }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        device_id = (TextView) getView().findViewById(R.id.device_id);
        sim_serial = (TextView) getView().findViewById(R.id.sim_serial);

        validate = (Button) getView().findViewById(R.id.validate_btn);




        device_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),Device_Id.class));
            }
        });

        sim_serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Sim_Serial.class));
            }
        });

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

    }


    private void QueryInputs() {
        class Query extends AsyncTask<Void, Void,String>
        {
            ProgressDialog waitingDialog = new ProgressDialog(getActivity());

            public ProgressDialog getWaitingDialog() {
                return waitingDialog;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               waitingDialog.setTitle("validating");
               waitingDialog.setMessage("Please Wait as we Validate Parameters");
               waitingDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating requesting parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("qr_number",device_id.getText().toString());
                params.put("serial_num",sim_serial.getText().toString());

                updateAttempts();

                //return the response
                return requestHandler.sendPostRequest(VALIDATING_QR_IMSI_URL,params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                waitingDialog.dismiss();

                try
                {
                    //converting response to json
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error"))
                    {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        View success_message = getLayoutInflater().inflate(R.layout.success_result,null);

                        final MaterialEditText editDeviceId = success_message.findViewById(R.id.edtDeviceId);
                        final MaterialEditText editSimSerial = success_message.findViewById(R.id.edtSim_card_Serial);
                        editDeviceId.setKeyListener(null);
                        editSimSerial.setKeyListener(null);

                        editSimSerial.setText("IMSI: "+obj.getString("imsi"));
                        editDeviceId.setText("DEVICE ID: "+obj.getString("qr_number"));

                        builder.setMessage("Parameters Validated Successfully")
                                .setView(success_message)
                                .setTitle("Success")
                                .setCancelable(true)
                                .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Fragment results = new ResultsFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, new ResultsFragment(),"results").commit();
                                    }
                                }).setNegativeButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                               emptyFields();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    else if (obj.getBoolean("error"))
                    {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Parameters Mismatch")
                                .setCancelable(true)
                                .setTitle("ERROR")
                                .setIcon(R.drawable.ic_error_red_24dp)
                                .setPositiveButton("TRY AGAIN",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        emptyFields();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    waitingDialog.dismiss();
                    Toast.makeText(getActivity(), "failed to connect", Toast.LENGTH_SHORT).show();
                }
            }
        }

        Query query = new Query();
        query.execute();
    }

    private void emptyFields() {
        device_id.setText("");
        sim_serial.setText("");
    }



    private void updateAttempts() {

       final User user = SharedPrefmanager.getInstance(getActivity()).getUser();
       String attempt = String.valueOf(user.getAttempts());
       int attempt_count = Integer.parseInt(attempt);
       int new_count = attempt_count + 1;
       final String final_attempt = String.valueOf(new_count);

       class UpdateAttempts extends AsyncTask<Void, Void,String>
       {


           @Override
           protected void onPreExecute() {
               super.onPreExecute();

           }

           @Override
           protected String doInBackground(Void... voids) {
               //creating request handler object
               RequestHandler requestHandler = new RequestHandler();

               //creating request parameters
               HashMap<String, String> params = new HashMap<>();
               params.put("email",user.getEmail());
               params.put("attempts",final_attempt);

               //return the response
               return  requestHandler.sendPostRequest(ATTEMPTS_UPDATE_URL,params);
           }

           @Override
           protected void onPostExecute(String s) {
               super.onPostExecute(s);

               try{
                   //converting response to json
                   JSONObject obj = new JSONObject(s);

                   if (!obj.getBoolean("error"))
                   {
                       Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                       JSONObject userJson = obj.getJSONObject("attempt_update");

                       //updating attempts

                       User user = new User();
                       user.setAttempts(userJson.toString());
                   }
                   else
                   {
                       Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                   }

               } catch (JSONException e) {
                   e.printStackTrace();

                   Toast.makeText(getActivity(), "could not connect", Toast.LENGTH_SHORT).show();
               }
           }
       }
       UpdateAttempts updateAttempts = new UpdateAttempts();
       updateAttempts.execute();
    }

    public void validateInputs() {
        final String Device_id = device_id.getText().toString();
        final String Sim_serial = sim_serial.getText().toString();

        if (TextUtils.isEmpty(Device_id))
        {
            Toast.makeText(getActivity(), "Please Scan The Device Id", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Sim_serial))
        {
            Toast.makeText(getActivity(), "Please Scan The Sim Serial", Toast.LENGTH_SHORT).show();
        }
        else
        {
            QueryInputs();
        }
    }
}
