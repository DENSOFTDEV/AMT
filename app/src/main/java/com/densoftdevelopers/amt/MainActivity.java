package com.densoftdevelopers.amt;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.densoftdevelopers.amt.Common.Common;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;



import static com.densoftdevelopers.amt.HomeActivity.LOGIN_URL;

public class MainActivity extends AppCompatActivity {

    private Button SignIn;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_main);

           saveLogin = SharedPrefmanager.getInstance(this).isLoggedIn();

          /* if (saveLogin == true)
            {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
            else
           {
               startActivity(new Intent(MainActivity.this, MainActivity.class));
           }*/

           SignIn = (Button) findViewById(R.id.btn_sign_in);
           SignIn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   displayAlertDialog();
               }
           });

    }

    public void displayAlertDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use an email address to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View signin_layout = inflater.inflate(R.layout.sign_in_layout,null);

        final MaterialEditText editEmail = signin_layout.findViewById(R.id.sign_in_edtEmail);
        final MaterialEditText editPassword = signin_layout.findViewById(R.id.sign_in_edtPassword);

        dialog.setView(signin_layout);

        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(editEmail.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please edit email is required", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(editPassword.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please edit email is required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dialog.dismiss();
                    class UserLogin extends AsyncTask<Void, Void, String>
                    {

                        ProgressDialog loadingBar = new ProgressDialog(MainActivity.this);

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            loadingBar.setTitle("Account verification");
                            loadingBar.setMessage("please wait as we verify your account");
                            loadingBar.show();
                        }

                        @Override
                        protected String doInBackground(Void... voids) {

                            //Creating request handler object
                            RequestHandler requestHandler = new RequestHandler();

                            //creating request parameters
                            HashMap<String, String> params = new HashMap<>();
                            params.put("email",editEmail.getText().toString());
                            params.put("password",editPassword.getText().toString());

                            //returning the response
                            return requestHandler.sendPostRequest(LOGIN_URL,params);
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            loadingBar.dismiss();

                            try {
                                //converting response to JSON Object
                                JSONObject obj = new JSONObject(s);

                                //if no error in response
                                if (!obj.getBoolean("error"))
                                {
                                    Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    //getting the new user from the response
                                    JSONObject userJson = obj.getJSONObject("user");

                                    //creating the new user object
                                    User user = new User(
                                            userJson.getString("name"),
                                            userJson.getString("email"),
                                            userJson.getString("phone"),
                                            userJson.getString("password"),
                                            userJson.getString("attempts")
                                    );

                                    //storing the user in shared preferences
                                    SharedPrefmanager.getInstance(getApplicationContext()).userLogin(user);

                                    //Start then Home Activity
                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                                }
                                else if (obj.getBoolean("error"))
                                {
                                    Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                                Toast.makeText(MainActivity.this, "failed could not connect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    UserLogin userLogin = new UserLogin();
                    userLogin.execute();
                }
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
               finish();
            }
        });

        dialog.show();

    }

}
