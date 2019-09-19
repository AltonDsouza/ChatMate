package com.dynashwet.chatmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dynashwet.chatmate.Models.profilegrid;
import com.dynashwet.chatmate.R;

import java.util.List;

/**
 * Created by Aws on 11/03/2018.
 */

public class ProfileGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<profilegrid> mData;
    RequestOptions option;

    public ProfileGridAdapter(Context mContext, List<profilegrid> mData) {
        this.mContext = mContext;
        this.mData = mData;

        option=new RequestOptions().fitCenter().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view ;
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        view = inflater.inflate(R.layout.singlegrid,parent,false) ;
//        final MyViewHolder viewHolder = new MyViewHolder(view) ;
//
//
//
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position)
//    {
//
//        if(mData.get(position).getPosttype().equals("text"))
//        {
//            holder.text.setText(mData.get(position).getText());
//        }
//       else if(mData.get(position).getPosttype().equals("image"))
//        {
//            Glide.with(mContext).load(mData.get(position).getImg()).apply(option).into(holder.logo);
//        }
//    }
//    @Override
//    public int getItemCount() {
//        return mData.size();
//    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

//    public static class MyViewHolder extends  RecyclerView.ViewHolder{
//
//        TextView salonname,landmark,review,date;
//        RatingBar service,staff,hygiene;
//        //ImageView img_thumbnail;
//
//        ImageView logo;
//        TextView text;
//        LinearLayout view_container;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
//
//            view_container = itemView.findViewById(R.id.container);
//            logo  = itemView.findViewById(R.id.image);
//            text  = itemView.findViewById(R.id.text);
//
//
//        }
//    }

}

