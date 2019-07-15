package com.densoftdevelopers.amt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.densoftdevelopers.amt.Common.Common;
import com.densoftdevelopers.amt.Fragments.ScanFragment;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    public static final String LOGIN_URL = "http://178.128.114.85/app_files/Backend/login.php";
    public static final String ADD_USER_URL = "http://178.128.114.85/app_files/Backend/register.php";
    public static  final String REPORT_SEND_URL = "https://densoftdevelopers.com/mail/AdminReport.php";
    public static final  String VALIDATING_QR_IMSI_URL = "http://178.128.114.85/app_files/Backend/validate.php";
    public static  final String  RESULTS_FETCH = "http://178.128.114.85/app_files/Backend/fetch.php";
    public static final String ATTEMPTS_UPDATE_URL = "http://178.128.114.85/app_files/Backend/attempts.php";


    private static  final int MY_CAMERA_REQUEST_CODE = 100;
    private static  final int MY_INTERNET_REQUEST_CODE = 100;
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    public String greeting;
    TextView account_details;





    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  checkUserLogin();
        setContentView(R.layout.activity_home);
        ButterKnife.bind(HomeActivity.this);


        toolbar = (Toolbar) findViewById(R.id.top_navigation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setOnMenuItemClickListener(menuItemSelectedListener);

        account_details = (TextView) findViewById(R.id.profile_settings);
        getTimeSalutation();







        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ScanFragment(),"scan").commit();

        checkPermissions();

    }

    private void getTimeSalutation() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int hour = cal.get(Calendar.HOUR_OF_DAY);

        if(hour>= 12 && hour < 17){
            greeting = "Good Afternoon";
        } else if(hour >= 17 && hour < 21){
            greeting = "Good Evening";
        } else if(hour >= 21 && hour < 24){
            greeting = "Good Night";
        } else {
            greeting = "Good Morning";
        }

        User user = SharedPrefmanager.getInstance(this).getUser();
        account_details.setText(" "+greeting+": "+user.getName());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions( new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        else if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions( new String[]{Manifest.permission.INTERNET}, MY_INTERNET_REQUEST_CODE);
        }
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "camera permission denied, this app will not work", Toast.LENGTH_LONG).show();
            }

        }
        else if (requestCode == MY_INTERNET_REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Internet permission granted", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "Internet permission denied, this app will not work", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return true;
    }



    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                    switch (menuItem.getItemId())
                    {
                        case  R.id.scan:
                            selectedFragment = new ScanFragment();
                            break;
                        case R.id.report:
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                                if (currentFragment.getTag().equals("scan"))
                                {
                                    Toast.makeText(HomeActivity.this, "scan device qr and sim serial to proceed", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(HomeActivity.this, "you are already in report screen", Toast.LENGTH_SHORT).show();
                                }
                            break;
                    }
                    if (selectedFragment != null)
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }
                    return true;
                }
            };

    private Toolbar.OnMenuItemClickListener menuItemSelectedListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId())
            {
                case R.id.logout:
                    Toast.makeText(HomeActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.settings:
                    startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                    break;
            }
            return true;
        }
    };

    private void accountDialogShow() {
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
        dialog.setTitle("ACCOUNT DETAILS");
        dialog.setMessage("You may update your account details");

        LayoutInflater inflater = LayoutInflater.from(this);
        View account_layout = inflater.inflate(R.layout.account_layout,null);

        final EditText username = (EditText) findViewById(R.id.edt_user_name);
        final EditText useremail = (EditText) findViewById(R.id.edt_user_email);
        final EditText userphone = (EditText) findViewById(R.id.edt_user_phone);
        final EditText userpassword = (EditText) findViewById(R.id.edt_user_password);
        final EditText userconfrimpass = (EditText) findViewById(R.id.edt_user_confirm_pass);

        User user = new User();

       // username.setText(user.getName());
      //  useremail.setText(user.getEmail());
      //  userphone.setText(user.getPhone());
      //  userpassword.setText(user.getPassword());
      //  userconfrimpass.setText("");

        dialog.setView(account_layout);
        dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private void printKeyHash() {
        try{
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES
            );
            for (Signature signature : packageInfo.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
