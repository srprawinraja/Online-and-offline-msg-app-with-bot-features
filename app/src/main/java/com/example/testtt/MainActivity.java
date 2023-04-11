package com.example.testtt;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    Intent i;
    EditText nameedit;
    String data;
    ActivityResultLauncher<String[]> permission;
    private boolean  contact=false;
    private boolean storage=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button namebutton;


        namebutton=findViewById(R.id.buttonbutton);
        nameedit=findViewById(R.id.textView6);

        namebutton.setOnClickListener(v -> {
           String name= nameedit.getText().toString();
           String ph = getIntent().getStringExtra("lastkey");

            storedata(name,ph);
            data= getIntent().getStringExtra("data");
            i = new Intent(getApplicationContext(),home_activity.class);
            i.putExtra("key1",name);
            i.putExtra("data",data);
            startActivity(i);
        });
        permission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if(result.get(Manifest.permission.READ_CONTACTS)!=null){
                    contact= Boolean.TRUE.equals(result.get(Manifest.permission.READ_CONTACTS));
                }
                if(result.get(Manifest.permission.READ_EXTERNAL_STORAGE)!=null){
                    storage= Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
                }
            }
        });
        requestonpermission();
    }

    private void storedata(String name, String ph) {
        FirebaseDatabase root = FirebaseDatabase.getInstance();
        DatabaseReference reference = root.getReference("users");

        reference.child(ph).setValue(name);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestonpermission() {
        contact= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED;
        storage= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;

        int pp= PackageManager.PERMISSION_GRANTED;
        Log.d("prawin",String.valueOf(pp));
        List<String> permissonRequest= new ArrayList<>();
        if(!contact){
            permissonRequest.add(Manifest.permission.READ_CONTACTS);
        }
        if(!storage){
            permissonRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(!permissonRequest.isEmpty()){
            permission.launch(permissonRequest.toArray(new String[0]));
        }

    }


}