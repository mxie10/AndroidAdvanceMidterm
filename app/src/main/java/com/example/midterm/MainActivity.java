package com.example.midterm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

    private MessageAdapter adapter;
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
            Intent intent = new Intent(this, ChatService.class);
            intent.putExtras(data);
            startService(intent);

            //add message to list and get response from bot
            messagesList.add(new Message("SEND", message));
            et_message.setText("");
            rv_messages.scrollToPosition(adapter.getItemCount() - 1);
            botResponse(message);
        }
    }

    public void botResponse(String message){
        int time = (int) (System.currentTimeMillis());
        Timestamp timestamp = new Timestamp(time);

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