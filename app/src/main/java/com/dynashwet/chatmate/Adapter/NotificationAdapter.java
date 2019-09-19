package com.dynashwet.chatmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dynashwet.chatmate.Chat;
import com.dynashwet.chatmate.Models.Notification;
import com.dynashwet.chatmate.NavigationDashboard.ProfileActivity;
import com.dynashwet.chatmate.PostComment;
import com.dynashwet.chatmate.R;

import java.util.List;


/*

Author : Alton Dsouza
 */
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends BaseAdapter {
    private Context context;
    private List<Notification> list;
    RequestOptions requestOptions;
    String fragment;
    String UID;

    public NotificationAdapter(Context context, List<Notification> list, String comingFromFragment) {
        this.context = context;
        this.list = list;
        this.fragment = comingFromFragment;
        requestOptions =new RequestOptions().fitCenter().placeholder(R.drawable.defaultcomment).error(R.drawable.defaultcomment);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateList(List<Notification> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.notification_item, viewGroup, false);

        TextView title = view.findViewById(R.id.title);
        TextView message = view.findViewById(R.id.message);
        CircleImageView proPic = view.findViewById(R.id.notiProfileImage);

        SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");


            title.setText(list.get(position).getTitle());
            message.setText(list.get(position).getMessage());
            Glide.with(context).load(list.get(position).getImage()).apply(requestOptions).into(proPic);

            view.setOnClickListener(action->{
                //Redirect to post
//                Toast.makeText(context, "Work in progress", Toast.LENGTH_SHORT).show();

                if(list.get(position).getTitle().equals("Message"))
                {
                    Intent intent = new Intent(context.getApplicationContext(), Chat.class);
                    intent.putExtra("from", UID);//From
                    intent.putExtra("friendid", list.get(position).getFromID());
                    intent.putExtra("to",  list.get(position).getFromID());//The one who sent you message
                    intent.putExtra("name", list.get(position).getName());
                    intent.putExtra("dp", list.get(position).getImage());
                    context.startActivity(intent);
                }
                else if(list.get(position).getTitle().equals("Comment"))
                {
                    Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("pidComingFromNotification", list.get(position).getPostID());
                    intent.putExtra("UIDcomingFromNotification", list.get(position).getFromID());
                    context.startActivity(intent);

                }
                else if(list.get(position).getTitle().equals("Like")){
                    Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);//Like here
                    intent.putExtra("pidComingFromNotification", list.get(position).getPostID());
                    intent.putExtra("UIDcomingFromNotification", list.get(position).getFromID());
                    context.startActivity(intent);
                }

                else if(list.get(position).getTitle().equals("Friend Request")){
                    Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("UIDFriendRequest", list.get(position).getFromID());
                    context.startActivity(intent);
                }
                //Dyna-UserID, Dyna-PostID

            });




        return view;
    }
}