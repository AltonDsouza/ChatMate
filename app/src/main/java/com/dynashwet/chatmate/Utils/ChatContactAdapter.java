package com.dynashwet.chatmate.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dynashwet.chatmate.Models.ChatMessage;
import com.dynashwet.chatmate.Models.Contacts;
import com.dynashwet.chatmate.R;
import com.squareup.picasso.Picasso;

import java.util.Base64;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatContactAdapter extends BaseAdapter {
    private Context context;
    private List<Contacts> chatUser;

    public ChatContactAdapter(Context context, List<Contacts> chatUser) {
        this.context = context;
        this.chatUser = chatUser;
    }

    @Override
    public int getCount() {
        return chatUser.size();
    }

    @Override
    public Object getItem(int position) {
        return chatUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=View.inflate(context, R.layout.contact_item,null);

//        LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        TextView textView = view.findViewById(R.id.text_contact_name);
        CircleImageView circleImageView = view.findViewById(R.id.image_contact_profile);

        try{
            textView.setText(chatUser.get(position).getName());
            Picasso.with(context).load(chatUser.get(position).getImage()).into(circleImageView);
        }
        catch (NullPointerException ex){
            Toast.makeText(context, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;
    }
}
