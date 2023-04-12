package com.example.chatme.chat;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.chatme.R;

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
