package com.dynashwet.chatmate.Adapter;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.Models.Comments;
import com.dynashwet.chatmate.Models.NewComment;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*

Author : Alton Dsouza
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private Context mContext;
    private List<NewComment> mData;
    RequestOptions option;
    private Mediator mediator;

    public CommentAdapter(Context mContext, List<NewComment> mData) {
        this.mContext = mContext;
        this.mData = mData;

        option=new RequestOptions().fitCenter().placeholder(R.drawable.defaultcomment).error(R.drawable.defaultcomment);
        mediator = new Mediator(this.mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.single_comment,parent,false);
        final MyViewHolder viewHolder = new MyViewHolder(view) ;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.username.setText(mData.get(position).getName());
        holder.comment.setText(mData.get(position).getReply());
     Glide.with(mContext).load(mData.get(position).getImage()).apply(option).into(holder.img);
//        Picasso.with(mContext).load(mData.get(position).getImage()).into(holder.img);

        holder.itemView.setOnClickListener(view->{//Delete Comment
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
            builder.setMessage("Delete Comment?");
            builder.setPositiveButton("Yes", (dialogInterface, pos) ->  {
//                Toast.makeText(mContext, "Clicked "+position, Toast.LENGTH_SHORT).show();
                mediator.delComment(mData.get(position).getCommentID(), mData.get(position).getPID(),

                        mData.get(position).getUser_id(), result ->   { //ComID, PostID, UID

                            JSONObject object = new JSONObject(result);
                            String msg = object.getString("Msg");

                            if(msg.equals("Success")){

                                mData.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeRemoved(position, mData.size());

                            }
                            else if(msg.equals("Error")){
                                String reason = object.getString("Reason");
                                if(reason.equals("NoComment")){//Comment already deleted

                                }else if(reason.equals("Restrict")){//User cannot delete comment

                                }
                            }
                });
            });

            builder.setNegativeButton("No", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });

            AlertDialog alertDialog = builder.create();

            if(mData.get(position).getCommentBy().equals(mData.get(position).getUser_id())) {alertDialog.show();}
            else if(mData.get(position).getUploadBy().equals(mData.get(position).getUser_id())) {alertDialog.show();}


        });
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView username,comment;
        CircleImageView img;
        LinearLayout view_container;

        public MyViewHolder(View itemView) {
            super(itemView);


            //view_container = itemView.findViewById(R.id.container);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            img = itemView.findViewById(R.id.img);

        }
    }

}
