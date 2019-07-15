package com.densoftdevelopers.amt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;

import com.densoftdevelopers.amt.Fragments.ScanFragment;
import com.google.zxing.Result;
import com.suke.widget.SwitchButton;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Device_Id extends AppCompatActivity implements ZXingScannerView.ResultHandler, SwitchButton.OnCheckedChangeListener {

    ZXingScannerView scannerView;
    SwitchButton flashlight, shutter_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(R.layout.activity_device__id);
        CardView camerashow = (CardView) findViewById(R.id.camera_layout);
        camerashow.addView(scannerView);
        scannerView.setSoundEffectsEnabled(true);
        scannerView.setAutoFocus(true);
        scannerView.setFlash(true);


        flashlight = (SwitchButton) findViewById(R.id.switch_flash_light);
        flashlight.setOnCheckedChangeListener(this);

        shutter_sound = (SwitchButton) findViewById(R.id.switch_flash_light);
        shutter_sound.setOnCheckedChangeListener(this);

    }

    @Override
    public void handleResult(Result result) {

        ScanFragment.device_id.setText(result.getText());
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {

        if (flashlight.isChecked())
        {

        }

    }
}
