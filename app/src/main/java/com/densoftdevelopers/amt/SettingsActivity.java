package com.densoftdevelopers.amt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

public class SettingsActivity extends AppCompatActivity {

    private Button  edit_details, SaveDetails;
    private ImageView arrowBack;
    private MaterialEditText edtEmail, edtName, edtPhone, edtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edit_details = (Button) findViewById(R.id.account_editDetails);
        SaveDetails = (Button) findViewById(R.id.account_saveDetails);

        arrowBack = (ImageView) findViewById(R.id.account_arrow_back);
        edtEmail = (MaterialEditText) findViewById(R.id.account_edtEmail);
        edtName = (MaterialEditText) findViewById(R.id.account_name);
        edtPhone = (MaterialEditText) findViewById(R.id.account_phone);
        edtPassword = (MaterialEditText) findViewById(R.id.account_edtPassword);

        User user = SharedPrefmanager.getInstance(this).getUser();

        edtEmail.setText(user.getEmail());
        edtName.setText(user.getName());
        edtPhone.setText(user.getPhone());
        edtPassword.setText(user.getPassword());

        edtEmail.setKeyListener(null);
        edtName.setKeyListener(null);
        edtPhone.setKeyListener(null);
        edtPassword.setKeyListener(null);

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Back to Home", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
            }
        });

        edit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEmail.setKeyListener(new MaterialEditText(getApplicationContext()).getKeyListener());
                edtName.setKeyListener(new MaterialEditText(getApplicationContext()).getKeyListener());
                edtPhone.setKeyListener(new MaterialEditText(getApplicationContext()).getKeyListener());
                edtPassword.setKeyListener(new MaterialEditText(getApplicationContext()).getKeyListener());;
            }
        });
    }
}
