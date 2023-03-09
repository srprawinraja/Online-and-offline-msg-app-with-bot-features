package com.example.testtt;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Signup extends AppCompatActivity {
    String phnum;

    EditText text1,text2;
    Button next;

    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        text1=findViewById(R.id.editTextPhone);
        next=findViewById(R.id.button2);
        text2=findViewById(R.id.countrycode);




        next.setOnClickListener(view -> {
            phnum=text1.getText().toString();

                if(phnum.length()==10) {

                    i = new Intent(getApplicationContext(), login.class);
                    i.putExtra("keyName",text2.getText().toString()+ phnum);
                    startActivity(i);
                }
                else{
                    Toast.makeText(Signup.this,"enter the correct number",Toast.LENGTH_LONG).show();
                }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentuser!=null){
            startActivity(new Intent(Signup.this,home_activity.class));
            finish();
        }
    }
}