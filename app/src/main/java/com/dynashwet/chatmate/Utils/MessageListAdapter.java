package com.dynashwet.chatmate.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dynashwet.chatmate.Models.ChatMessage;
import com.dynashwet.chatmate.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.Util;

public class MessageListAdapter extends RecyclerView.Adapter {

//    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
//    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static List<ChatMessage> chatMessages;
    private static Context context;

    public MessageListAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        switch (chatMessages.get(position).getType()) {
            case 1:
                return ChatMessage.SENT;
            case 2:
                return ChatMessage.RECEIVED;
            default:
                return -1;
        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView name, message_body, messageTime;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.image_message_profile);
            name = itemView.findViewById(R.id.text_message_name);
            message_body = itemView.findViewById(R.id.text_message_body);
            messageTime = itemView.findViewById(R.id.text_message_time);
        }

        void bind(final ChatMessage message) {
            Picasso.with(context).load(message.getUserImage()).into(circleImageView);
            name.setText(message.getMessageUser());
            message_body.setText(message.getMessageText());
            messageTime.setText(message.getMessageTime());
//            // Format the stored timestamp into a readable String using method.
//            timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        }
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView message, time;
        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.text_message_body);
            time = itemView.findViewById(R.id.text_message_time);
        }
        void bind(final ChatMessage chatMessage){
                message.setText(chatMessage.getMessageText());
                time.setText(chatMessage.getMessageTime());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case ChatMessage.SENT:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, viewGroup, false);
                return new SentMessageHolder(view);
            case ChatMessage.RECEIVED:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_received, viewGroup, false);
                return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        switch (chatMessages.get(position).getType()) {
            case ChatMessage.SENT:
                ((SentMessageHolder) viewHolder).bind(chatMessages.get(position));
                break;

            case ChatMessage.RECEIVED:
                ((ReceivedMessageHolder)viewHolder).bind(chatMessages.get(position));
                break;

        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


}
