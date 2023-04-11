package com.example.testtt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{

    String messageSenderId;
    private List<Messages> userMessagesList;
    private  FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    public MessageAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
        }
    }





    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            messageSenderId=currentUser.getPhoneNumber();

        } else {
            // user is not signed in
        }
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        if (fromMessageType.equals("text")){
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            if(fromUserID.equals(messageSenderId))
            {
                Log.i("vanakkam","vanakkam");
                holder.senderMessageText.setText(messages.getMessage());

            }
            else
            {
                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messages.getMessage());
            }
        }
        }

    @Override
    public int getItemCount() {

        return userMessagesList.size();
    }



}
