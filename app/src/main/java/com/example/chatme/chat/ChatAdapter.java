package com.example.chatme.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatme.R;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<ChatMessage>  {
    private final int USER_MESSAGE = 0;
    private final int BOT_MESSAGE = 1;

    private String currentDate = "";
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
            LayoutInflater inflater = LayoutInflater.from(context);

            if (layoutType == USER_MESSAGE) {
                convertView = inflater.inflate(R.layout.list_item_user, parent, false);
                viewHolder.messageTextView = convertView.findViewById(R.id.user_message_text);
                viewHolder.messageBackground = convertView.findViewById(R.id.user_message_background_shape);
                viewHolder.timeTextView = convertView.findViewById(R.id.user_message_time);
                viewHolder.imageView = convertView.findViewById(R.id.message_image);
            } else {
                convertView = inflater.inflate(R.layout.list_item_bot, parent, false);
                viewHolder.profileImageView = convertView.findViewById(R.id.bot_profile_image);
                viewHolder.messageTextView = convertView.findViewById(R.id.bot_message_text);
                viewHolder.messageBackground = convertView.findViewById(R.id.bot_message_background_shape);
                viewHolder.timeTextView = convertView.findViewById(R.id.bot_message_time);
                viewHolder.imageView = convertView.findViewById(R.id.message_image);

            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatMessage message = messages.get(position);
        viewHolder.messageTextView.setText(message.getMessage());
        // Format and display the time
        String time = formatTime(message.getTimeStamp());
        viewHolder.timeTextView.setText(time);

        if (message.hasImage()) {
            // Display the image
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.messageTextView.setVisibility(View.GONE);
            if (viewHolder.messageBackground != null) {
                viewHolder.messageBackground.setVisibility(View.GONE);
            }
            Bitmap imageBitmap = message.getImageBitmap();
            viewHolder.imageView.setImageBitmap(imageBitmap);
        } else {
            // Hide the image view if there is no image
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.messageTextView.setVisibility(View.VISIBLE);
            if (viewHolder.messageBackground != null) {
                viewHolder.messageBackground.setVisibility(View.VISIBLE);
            }
        }

        if (viewHolder.messageBackground != null) {
            viewHolder.messageBackground.setBackgroundResource(message.getBackgroundColor());
        }

        return convertView;
    }



    private static class ViewHolder {
        TextView messageTextView;
        View messageBackground;
        TextView timeTextView;
        ImageView imageView;
        ImageView profileImageView;
    }

    private String formatTime(String timestamp) {
        // Assuming timestamp format is "yyyy-MM-dd HH:mm"
        String[] parts = timestamp.split(" ");
        String date = parts[0];
        String time = parts[1];

        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        String period;
        if (hour < 12) {
            period = "오전";
        } else {
            period = "오후";
            if (hour > 12) {
                hour -= 12;
            }
        }

        String minuteFormatted = (minute < 10) ? "0" + minute : String.valueOf(minute);

        return period + " " + hour + ":" + minuteFormatted;
    }

}