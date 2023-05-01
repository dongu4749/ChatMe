package com.example.chatme.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chatme.R;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<ChatMessage>  {
    private final int USER_MESSAGE = 0;
    private final int BOT_MESSAGE = 1;

    private Context context;
    private ArrayList<ChatMessage> messages;

    public ChatAdapter(Context context, ArrayList<ChatMessage> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return messages.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isSentByUser()) {
            return USER_MESSAGE;
        } else {
            return BOT_MESSAGE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int layoutType = getItemViewType(position);


        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (layoutType == USER_MESSAGE) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
                viewHolder.messageTextView = convertView.findViewById(R.id.user_message_text);
                viewHolder.messageBackground = convertView.findViewById(R.id.user_message_background_shape);
                viewHolder.timeTextView = convertView.findViewById(R.id.user_message_time);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_bot, parent, false);
                viewHolder.messageTextView = convertView.findViewById(R.id.bot_message_text);
                viewHolder.messageBackground = convertView.findViewById(R.id.bot_message_background_shape);
                viewHolder.timeTextView = convertView.findViewById(R.id.bot_message_time);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatMessage message = messages.get(position);
        viewHolder.messageTextView.setText(message.getMessage());
        viewHolder.timeTextView.setText(message.getTimeStamp());

        if (viewHolder.messageBackground != null) { // messageBackground이 null이 아닌 경우에만 setBackgroundResource 호출
            viewHolder.messageBackground.setBackgroundResource(message.getBackgroundColor());
        }



        return convertView;
    }


    private static class ViewHolder {
        TextView messageTextView;
        View messageBackground;
        TextView timeTextView;
    }
}