package com.example.midterm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.midterm.model.Message;
import com.example.midterm.service.ChatService;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel_name";
    private static final String CHANNEL_DESC = "channel_description";
    private MessageAdapter adapter;
    private BroadcastReceiver receiver;
    RecyclerView rv_messages;
    Button btn_send;
    EditText et_message;
    List<Message> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_messages = (RecyclerView)findViewById(R.id.rv_messages);
        btn_send = (Button)findViewById(R.id.btn_send);
        et_message = (EditText)findViewById(R.id.et_message);
        setRecyclerView();
        setClickEvents();
        receiveBroadCast();
    }
    public void setRecyclerView(){
        adapter = new MessageAdapter(this,messagesList);
        rv_messages.setAdapter(adapter);
        rv_messages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    public void setClickEvents(){
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void sendMessage(){
        String message = et_message.getText().toString();
        if(!message.isEmpty()) {
            //send broadcast
            Bundle data = new Bundle();
            data.putInt(ChatService.CMD, ChatService.CMD_SEND_MESSAGE);
            data.putString(ChatService.KEY_MESSAGE_TEXT, message);
            messagesList.add(new Message("SEND", message));
            et_message.setText("");
            rv_messages.scrollToPosition(adapter.getItemCount() - 1);

            Intent intent = new Intent(this, ChatService.class);
            intent.putExtras(data);
            startService(intent);
            //get broadcast content
            //send notification
            sendNotification(message);
        }

    }

    private void receiveBroadCast(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_NEW_MESSAGE)) {
                    String message = intent.getStringExtra(Constants.CHAT_MESSAGE);
                    botResponse(message);
                }
            }
        };

        // Register the BroadcastReceiver to receive the MY_BROADCAST action
        IntentFilter filter = new IntentFilter(Constants.BROADCAST_NEW_MESSAGE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver
        unregisterReceiver(receiver);
    }

    public void sendNotification(String message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android Oreo and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Send Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }

    public void botResponse(String message){
        Log.v("botResponse", "in botResponse???");
        new Thread(() -> {
            //Fake response delay
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                //Gets the response
                String response1 = "";
                String response2 = "";
                String response3 = "";
                String otherResponse = "";
                if(message.contains("Kevin")){
                    response1 = "Hello Kevin!";
                    response2 = "How are you?";
                    response3 = "Good Bye Kevin!";
                    messagesList.add(new Message("RECEIVE",response1));
                    messagesList.add(new Message("RECEIVE",response2));
                    messagesList.add(new Message("RECEIVE",response3));
                }else{
                    otherResponse = "Sorry, I don't understand.";
                    messagesList.add(new Message("RECEIVE",otherResponse));

                }
                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.getItemCount() - 1);

            });
        }).start();
    }
}