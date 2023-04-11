package com.example.testtt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class BluetoothSearch extends AppCompatActivity {
    private ListView available_devices;
    private ArrayAdapter<String> adapterpairedevice;
    private Button Listen;
    private ProgressBar progress;
   private  ImageButton searchAll;

   private  BluetoothDevice[] btArray;

    public String dev_address;

    public int index=0;


   private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_search);
        init();

        btArray=new BluetoothDevice[70];
        searchAll.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                adapterpairedevice.clear();
                Toast.makeText(getApplicationContext(), "search started", Toast.LENGTH_SHORT).show();

                if(bluetoothAdapter.isDiscovering()){
                    Log.i("america", "america");
                    bluetoothAdapter.cancelDiscovery();
                }

                bluetoothAdapter.startDiscovery();

            }
        });


    }
    BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            Log.i("hiiii","vanakkam");
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(index<=1) {
                    btArray[index] = device;
                    index+=1;
                }

                Log.i("america", String.valueOf(device));
                Log.i("pig", Arrays.toString(btArray));
                dev_address=device.getAddress();

                adapterpairedevice.add(device.getName());
            }
            if(bluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                progress.setVisibility(View.INVISIBLE);
                if(adapterpairedevice.getCount()==0) {
                    progress.setVisibility((View.INVISIBLE));
                    Toast.makeText(context, "no device found", Toast.LENGTH_SHORT).show();
                }
                else{
                    progress.setVisibility((View.INVISIBLE));
                    Toast.makeText(context, "device found", Toast.LENGTH_SHORT).show();
                }

            }

        }
    };


    private void init() {
        progress=findViewById(R.id.progress);
        available_devices=findViewById(R.id.list);
        searchAll=findViewById(R.id.bluetooth_button);

        adapterpairedevice= new ArrayAdapter<String> (getApplicationContext(),R.layout.list_view_item);

        available_devices.setAdapter(adapterpairedevice);




        available_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info=((TextView) view).getText().toString();

                Intent i=new Intent(getApplicationContext(),BluetoothChatting.class);
                i.putExtra("device_name",info);
                i.putExtra("device_address",dev_address);

                i.putExtra("option",btArray[position]);


                startActivity(i);

            }
        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter fill= new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothDeviceListener , fill);

        IntentFilter fill1= new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListener , fill1);
    }
}