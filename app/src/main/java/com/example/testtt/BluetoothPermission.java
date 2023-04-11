package com.example.testtt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BluetoothPermission extends AppCompatActivity {
    private Button BluetoothPermission;
    boolean check=false;
    private Intent i;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_permission);
        BluetoothPermission=findViewById(R.id.button4);

        BluetoothPermission.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if(!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                }

                if(mBluetoothAdapter.getScanMode()!= BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    Intent discover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discover.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,1000);
                    startActivity(discover);
                    check=false;
                }
                requestpermission();

            }

        });


    }

    private void requestpermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            int BLUETOOTH_PERMISSION_CODE = 101;

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, BLUETOOTH_PERMISSION_CODE);
        }
        else{
            if(check) {
                i = new Intent(getApplicationContext(), BluetoothSearch.class);

                startActivity(i);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(mBluetoothAdapter.isEnabled()) {
                i = new Intent(getApplicationContext(), BluetoothSearch.class);

                startActivity(i);
            }
            else if(!mBluetoothAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(), "enable your bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }
}