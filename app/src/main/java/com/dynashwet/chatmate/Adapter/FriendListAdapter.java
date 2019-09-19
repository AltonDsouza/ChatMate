package com.dynashwet.chatmate.Adapter;


import android.content.Context;

import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dynashwet.chatmate.Models.Singlefriend;
import com.dynashwet.chatmate.R;

import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private List<Singlefriend>mData;
    private Context context;
    RequestOptions option;




    public FriendListAdapter(List<Singlefriend> actorsLists, Context context) {
        this.mData = actorsLists;
        this.context = context;
        //this.swiper = swiper;

        option=new RequestOptions().fitCenter().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        // View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_list,parent,false);
        // final ViewHolder viewHolder = new ViewHolder(v);

        View view ;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.singlefriend,parent,false) ;
        final ViewHolder viewHolder = new ViewHolder(view) ;
        viewHolder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });




        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //ActorsList actorsList=actorsLists.get(position);

        holder.name.setText(mData.get(position).getName());

        Glide.with(context).load(mData.get(position).getImg()).apply(option).into(holder.img_thumbnail);
        /*swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

   /* private void refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mData.add(0, mData.get(new Random().nextInt(mData.size())));
                ActorsAdapter.this.notifyDataSetChanged();

                swiper.setRefreshing(false);

            }
        },3000);
    }*/






    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView aImg;
        private TextView name,followers,follow ;
        CircleImageView img_thumbnail;
        LinearLayout view_container;
        public ViewHolder(View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.container);
            img_thumbnail = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);

        }



    }




}
