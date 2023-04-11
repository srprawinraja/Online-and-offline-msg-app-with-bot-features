package com.example.testtt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

public class home_activity extends AppCompatActivity {
    private long lastClickTime = 0;
    Intent i;
    TextView textView;
    ImageButton button;
    String data;
    ListView list;
    private ImageButton blue;
    String pass1;
    private RelativeLayout relativeLayout;
    String pass2;
    ProgressBar pro;
    String[] don;
    String mark_name;
    String mark_myNum;
    String mark_number;
    private boolean hello=false;
    private Boolean bool=false;
    Integer[] don2={
            R.drawable.ic_baseline_person_24
    };

    public  int r []={R.drawable.ic_baseline_person_24};
    String uidd;
    public int count=0;
    private View view;
    private TextToSpeech textToSpeech;
    public Vector<String> stored_contact = new Vector<String>();
    public  Vector<String> contact_ph = new Vector<String>();
    public  Vector<String> contact_name = new Vector<String>();
    public  Vector<String> selected_contact = new Vector<String>();
    public  Vector<String> selected_name = new Vector<String>();
    String spokenText;

    @Override
    protected void onStart() {
        pro=findViewById(R.id.progressBar);

        selected_contact.clear();
        pro.setVisibility(View.VISIBLE);
        int BLUETOOTH_PERMISSION_CODE = 101;

        requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, BLUETOOTH_PERMISSION_CODE);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            display_contact();



        }
        else{

        }
        super.onStart();
    }

    private void final_contact() {
        int p1,p2;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
             uidd=currentUser.getPhoneNumber();

        } else {
            // user is not signed in
        }

        for(int i=0;i<stored_contact.size();i++){
            for(int j=0;j<contact_ph.size();j++){
                pass1=stored_contact.get(i);
                pass2=contact_ph.get(j);

                if(pass1.substring(uidd.length()-6,uidd.length()).equals(uidd.substring(uidd.length()-6,uidd.length()))){   break;}
                p1=pass1.length();
                p2=pass2.length();
                if(pass2.length()>5) {
                    if (pass1.substring(p1 - 1).equals(pass2.substring(p2 - 1))) {
                        if (pass1.substring(p1 - 2).equals(pass2.substring(p2 - 2))) {

                            if (pass1.substring(p1 - 3).equals(pass2.substring(p2 - 3))) {
                                if (pass1.substring(p1 - 4).equals(pass2.substring(p2 - 4))) {
                                    if (pass1.substring(p1 - 5).equals(pass2.substring(p2 - 5))) {

                                        selected_contact.add(contact_name.get(j));
                                        selected_name.add(stored_contact.get(i));
                                        count+=1;

                                        break;
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }

        don  = selected_contact.toArray(new String[selected_contact.size()]);
        geek gk=new geek(this, don, don2);
        list=(ListView)findViewById(R.id.list);
        pro.setVisibility(View.GONE);
        list.setAdapter(gk);



    }

    private void display_contact() {
        stored_contact.clear();
        contact_ph.clear();
        contact_ph.clear();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {



            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    stored_contact.add(dataSnapshot.getKey());
                }



                addContacts();
                final_contact();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void addContacts(){


        try {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            while (phones.moveToNext()) {
                @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contact_name.add(name);
                contact_ph.add(phoneNumber);

            }
            phones.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 20 && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
           spokenText = results.get(0);
            if(bool.equals(true)){

                Intent i=new Intent(getApplicationContext(),chat_ui.class);
                i.putExtra("name",mark_name);
                i.putExtra("mess",spokenText);
                i.putExtra("phone_num",mark_number);
                i.putExtra("sender",mark_myNum);
                i.putExtra("decider",true);
                startActivity(i);

            }
            if(!bool) {
                textToSpeech.speak("ok pls wait while i check", TextToSpeech.QUEUE_FLUSH, null, null);


                String nm;
                for (int i = 0; i < selected_contact.size(); i++) {
                    nm = selected_contact.get(i);
                    nm = nm.substring(0, nm.length() - 1);
                    spokenText = spokenText.substring(0, spokenText.length() - 1);
                    if (nm.equalsIgnoreCase(spokenText)) {
                        selected_contact.get(i);
                        mark_name=selected_contact.get(i);
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            mark_myNum=currentUser.getPhoneNumber();

                        } else {
                            // user is not signed in
                        }
                        mark_number=selected_name.get(i);


                        textToSpeech.speak("ah i found the person.what is your message?", TextToSpeech.QUEUE_FLUSH, null, null);
                        try {

                            Thread.sleep(3000); // 5000 milliseconds = 5 seconds
                            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            startActivityForResult(intent, 20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                       bool=true;
                        break;
                    } else {
                        textToSpeech.speak("sorry i can't find", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }

            // Do something with the transcribed text...
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        hello=getIntent().getBooleanExtra("decider", false);
        data= getIntent().getStringExtra("keyName");
       textView=findViewById(R.id.textView7);
        relativeLayout=findViewById(R.id.botLayout);

        button=findViewById(R.id.imageButton);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(),setting.class);
                startActivity(i);
            }
        });
        list=(ListView)findViewById(R.id.list);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    textToSpeech.speak("hi this is chit chat.whom would you want to message", TextToSpeech.QUEUE_FLUSH, null, null);
                try {
                    Thread.sleep(4000); // 5000 milliseconds = 5 seconds
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = (String) (list.getItemAtPosition(position));

                Intent i= new Intent(getApplicationContext(),chat_ui.class);
                i.putExtra("name",info);
                String no=selected_name.get(position);
                i.putExtra("phone_num",no);
                i.putExtra("sender",uidd);
                i.putExtra("decider",false);
                startActivity(i);
            }
        });
        blue=findViewById(R.id.blue);
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(getApplicationContext(),BluetoothPermission.class);
                startActivity(i);
            }
        });
    }

}