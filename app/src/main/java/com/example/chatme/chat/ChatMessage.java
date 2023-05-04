package com.example.chatme.chat;

import android.icu.text.SimpleDateFormat;

import com.example.chatme.R;

import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    private String message;
    private boolean sentByUser;
    public ChatMessage(String message, boolean sentByUser) {
        this.message = message;
        this.sentByUser = sentByUser;

    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

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
