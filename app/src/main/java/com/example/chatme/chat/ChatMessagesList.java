package com.example.chatme.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatMessagesList {
    private List<ChatMessage> chatMessageList;

    public ChatMessagesList() {
        chatMessageList = new ArrayList<>();
    }

    public void add(ChatMessage chatMessage) {
        chatMessageList.add(chatMessage);
    }

    public int size() {
        return chatMessageList.size();
    }

    public ChatMessage get(int index) {
        return chatMessageList.get(index);
    }
}
