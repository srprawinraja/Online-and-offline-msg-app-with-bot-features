package com.example.testtt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlueAdapter extends RecyclerView.Adapter<BlueAdapter.MessageViewHolder>{
    private List<BluetoothGetSet> userMessagesList;

    public BlueAdapter (List<BluetoothGetSet> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;



        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.bluetooth_sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.bluetooth_receiver_message_text);

        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetoothchatxml,parent,false);
       return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        BluetoothGetSet bluetoothGetSet = userMessagesList.get(position);
        String from=bluetoothGetSet.getFrom();

        Log.i("from",from);
        Log.i("fromm",bluetoothGetSet.getMessage());
        Log.i("  ","    ");
        holder.receiverMessageText.setVisibility(View.INVISIBLE);
        if(from.equals("sender")){
            holder.senderMessageText.setText(bluetoothGetSet.getMessage());

        }
        else{
            holder.senderMessageText.setVisibility(View.INVISIBLE);
            holder.receiverMessageText.setVisibility(View.VISIBLE);
            holder.receiverMessageText.setText(bluetoothGetSet.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
