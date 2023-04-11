package com.example.testtt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {
    FirebaseAuth mAuth;
    String code;
    Intent i;
    EditText textotp;
    String phnum,data;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
         data= getIntent().getStringExtra("keyName");


        TextView textViewlog;

        Button otpbutton;


        textViewlog=findViewById(R.id.textViewlogin);

        otpbutton=findViewById(R.id.otpnext);


        mAuth=FirebaseAuth.getInstance();

        sendverificationcode(data);

        otpbutton.setOnClickListener(view -> {
            textotp=findViewById(R.id.code);
            String otpdata=textotp.getText().toString();
            if(code.isEmpty()){

                Toast.makeText(login.this, "Enter the otp", Toast.LENGTH_SHORT).show();
            }
            else{
                verifycode(otpdata);
            }


        });


        textViewlog.append(data);
    }

    private void sendverificationcode(String phonenumber) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                    super.onCodeSent(s, forceResendingToken);
                    code=s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String smscode=phoneAuthCredential.getSmsCode();
                    if(smscode!=null){
                        textotp.setText(smscode);
                        verifycode(smscode);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    storedata(phnum);
                }
            };

    private void storedata(String phnum) {
        Toast.makeText(login.this,"verification failed",Toast.LENGTH_LONG).show();
    }

    private void verifycode(String smscode) {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(code,smscode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        i = new Intent(getApplicationContext(),MainActivity.class);
                        i.putExtra("lastkey",getIntent().getStringExtra("keyName"));
                        i.putExtra("data",data);
                        startActivity(i);

                        Toast.makeText(login.this, "verification completed", Toast.LENGTH_SHORT).show();
                    } else {

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(login.this, "verification failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


