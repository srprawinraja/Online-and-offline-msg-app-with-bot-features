package com.example.testtt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothChatting extends AppCompatActivity {
    private  String device_name,device_address;
    private TextView text,status;
    private  BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    private Button listen,connect;
    private EditText edit;
    SendReceive sendReceive;
    private ImageView send;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;
    private final List<BluetoothGetSet> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private BlueAdapter blueAdapter;
    private RecyclerView userMessagesList;
    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    Log.i("donee",tempMsg);

                    BluetoothGetSet bluetoothDevicee = new BluetoothGetSet("pvf",tempMsg);
                    messagesList.add(bluetoothDevicee);
                    blueAdapter.notifyDataSetChanged();
                    userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chatting);
        Intent i = getIntent();
        device_name = i.getStringExtra("device_name");
        device_address = i.getStringExtra("device_address");
        BluetoothDevice bluetoothDevice = getIntent().getExtras().getParcelable("option");
        text=findViewById(R.id.bluetooth_textView7);
        status=findViewById(R.id.bluetooth_textView2);
        send=findViewById(R.id.bluetooth_send);
        connect=findViewById(R.id.bluetooth_button6);
        if(device_name.length()>10) {
            text.setText(device_name.substring(0, 9));
        }
        else{ text.setText(device_name);
        }

        listen=findViewById(R.id.bluetooth_button5);
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        edit=findViewById(R.id.bluetooth_input_message);

        blueAdapter = new BlueAdapter(messagesList);

        userMessagesList=(RecyclerView) findViewById(R.id.bluetooth_cycle);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
      userMessagesList.setAdapter(blueAdapter);


        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientClass clientClass = new ClientClass(bluetoothDevice);
                clientClass.start();

                status.setText("connected");
            }
        });
        edit=findViewById(R.id.bluetooth_input_message);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string= String.valueOf(edit.getText());
                sendReceive.write(string.getBytes());
                String alpha=edit.getText().toString();
                BluetoothGetSet bluetoothDevice = new BluetoothGetSet("sender",alpha);
                messagesList.add(bluetoothDevice);
                blueAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                edit.setText("");



            }
        });
    }

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        @SuppressLint("MissingPermission")
        public ServerClass() {
                try {
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                 if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);
                    sendReceive=new SendReceive(socket);
                    sendReceive.start();


                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run()
        {

            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
                sendReceive=new SendReceive(socket);
                sendReceive.start();


            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}