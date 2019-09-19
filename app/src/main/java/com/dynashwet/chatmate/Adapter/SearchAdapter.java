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
import com.dynashwet.chatmate.Models.Notification;
import com.dynashwet.chatmate.NavigationDashboard.ProfileActivity;
import com.dynashwet.chatmate.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*

Author : Alton Dsouza
 */

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private List<Notification> list;
    RequestOptions requestOptions;
    String fragment;
    String UID;

    public SearchAdapter(Context context, List<Notification> list) {
        this.context = context;
        this.list = list;
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

            message.setVisibility(View.GONE);
            title.setText(list.get(position).getTitle());
//            message.setText(list.get(position).getMessage());
            Glide.with(context).load(list.get(position).getImage()).apply(requestOptions).into(proPic);

            view.setOnClickListener(action->{
                //Redirect to profile
                Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                intent.putExtra("UIDFromSearch", list.get(position).getFromID());
                context.startActivity(intent);

            });




        return view;
    }
}