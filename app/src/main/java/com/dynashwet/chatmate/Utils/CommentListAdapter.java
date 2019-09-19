package com.dynashwet.chatmate.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dynashwet.chatmate.Models.Comments;
import com.dynashwet.chatmate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends BaseAdapter {
    private static final String TAG = "CommentListAdapter";

    private Context mContext;
    private List<Comments> comments;

    public CommentListAdapter(Context mContext, List<Comments> comments) {
        this.mContext = mContext;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=View.inflate(mContext, R.layout.comment_layout,null);
//< get controls >
        TextView textview_contact_Name= (TextView) view.findViewById(R.id.comment_username);
        TextView comment= (TextView) view.findViewById(R.id.comment);
        CircleImageView circleImageView = (CircleImageView)view.findViewById(R.id.comment_profile_image);
//</ get controls >

//< show values >
        textview_contact_Name.setText(comments.get(position).getUsername());
        comment.setText(comments.get(position).getComment());
        Picasso.with(mContext).load(comments.get(position).getImage()).into(circleImageView);
//</ show values >


        view.setTag(comments.get(position).getUsername());
        return view;
    }
}
