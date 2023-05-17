package com.example.chatme.chat;

import android.graphics.Bitmap;

import com.example.chatme.R;

public class ChatMessage {
    private String message;
    private boolean sentByUser;
    private String timeStamp;

    private Bitmap imageBitmap;
    public ChatMessage(String message, boolean sentByUser, String timeStamp) {
        this.message = message;
        this.sentByUser = sentByUser;
        this.timeStamp = timeStamp;
    }
    public ChatMessage(Bitmap imageBitmap, boolean sentByUser, String timeStamp) {
        this.imageBitmap = imageBitmap;
        this.sentByUser = sentByUser;
        this.timeStamp = timeStamp;
    }
    public String getMessage() {
        return message;
    }
    public String getTimeStamp() {
        return timeStamp;
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
    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
}
