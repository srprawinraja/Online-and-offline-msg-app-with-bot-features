package com.example.testtt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class chat_ui extends AppCompatActivity {
    private String name;
    private String phone_num;
    private TextView textView,state;
    private Context context;
    private AppCompatImageView send_button;
    private EditText typed;
    private String typed_message;
    private DatabaseReference RootRef;
    private String messageReceiverID,messageSenderID;
    private FirebaseAuth mAuth;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView userMessagesList;
    private MessageAdapter messageAdapter;
    private ImageView  SendFilesButton;
    private  String myUrl="";
    private Uri fileUri;
    private StorageTask uploadTask;
    Boolean decide;
    private TextToSpeech textToSpeech;
    String mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ui);
        Intent i = getIntent();
        decide=getIntent().getBooleanExtra("decider", false);
        if(decide) {
            name = i.getStringExtra("name");
            messageReceiverID = i.getStringExtra("phone_num");
            messageSenderID = i.getStringExtra("sender");
            typed_message = i.getStringExtra("mess");

        }
        else{
            name = i.getStringExtra("name");
            messageReceiverID = i.getStringExtra("phone_num");
            messageSenderID = i.getStringExtra("sender");
        }
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

        textView = findViewById(R.id.textView7);
        state=findViewById(R.id.textView2);
        textView.setText(name);
        send_button=findViewById(R.id.send);
        typed=findViewById(R.id.input_message);
        RootRef = FirebaseDatabase.getInstance().getReference();
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.cycle);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        SendFilesButton=findViewById(R.id.gallery);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typed_message=typed.getText().toString();

                if(!typed_message.isEmpty()) {
                    send_message();
                }

            }
        });
        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ayoooo","ayooo");
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);

                intent.setType("image/*");
                Intent chooser = Intent.createChooser(intent, "Share this message");

                startActivityForResult(chooser,34);
            }
        });
        if(decide){
            send_message();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        messagesList.clear();
        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);

                messagesList.add(messages);

                messageAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void send_message() {
        Log.i("hello_receiver",messageReceiverID);
        Log.i("hello_receiverName",name);
        Log.i("hello_sender",messageSenderID);
        Log.i("hello_message",typed_message);
        String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
        String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

        DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                .child(messageSenderID).child(messageReceiverID).push();

        String messagePushID = userMessageKeyRef.getKey();

        Map messageTextBody = new HashMap();
        messageTextBody.put("message", typed_message);
        messageTextBody.put("type", "text");
        messageTextBody.put("from", messageSenderID);


        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
        messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {

                    Toast.makeText(getApplicationContext(), "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
                typed.setText("");
            }
        });
        if(decide){


                Intent intent=new Intent(getApplicationContext(),home_activity.class);
                intent.putExtra("hello",true);
                startActivity(intent);

        }
    }
}