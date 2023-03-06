package com.m.bot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.m.bot.Constants;

public class ChatService extends Service {

    public static final String CMD = "msg_cmd";
    private static final String TAG = "ChatService";
    public static final int CMD_SEND_MESSAGE = 10;
    public static final String KEY_MESSAGE_TEXT = "message_text";
    private PowerManager.WakeLock wakeLock;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            Bundle data = intent.getExtras();
            handleData(data);
        }
        return START_STICKY;
    }

    private void handleData(Bundle data) {
        int command = data.getInt(CMD);
        if (command == CMD_SEND_MESSAGE) {
            String messageText = (String) data.get(KEY_MESSAGE_TEXT);
            sendBroadcastNewMessage(messageText);
        }
    }

    private void sendBroadcastNewMessage(String message) {
        Log.d(TAG, "->(+)<- sending broadcast: BROADCAST_NEW_MESSAGE");
        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_NEW_MESSAGE);

        Bundle data = new Bundle();
        data.putString(Constants.CHAT_MESSAGE, message);
        intent.putExtras(data);

        sendBroadcast(intent);
    }

}
