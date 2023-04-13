package com.example.chatme.chat;

import android.icu.text.SimpleDateFormat;

import com.example.chatme.R;

import java.util.Locale;

public class ChatMessage {
    private String message;
    private boolean sentByUser;

//    private long messageTime;


    public ChatMessage(String message, boolean sentByUser) {
        this.message = message;
        this.sentByUser = sentByUser;
//        this.messageTime = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

//    public long getMessageTime() {
//        return messageTime;
//    }

    public boolean isSentByUser() {
        return sentByUser;
    }

    public int getBackgroundColor() {
        if (isSentByUser()) {
            return R.color.user_message_background_color;
        } else {
            return R.color.bot_message_background_color;
        }
    }
}
