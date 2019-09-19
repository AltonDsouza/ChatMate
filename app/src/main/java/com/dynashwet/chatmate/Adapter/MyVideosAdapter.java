package com.dynashwet.chatmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.autoplayvideos.AAH_CustomViewHolder;
import com.allattentionhere.autoplayvideos.AAH_VideoImage;
import com.allattentionhere.autoplayvideos.AAH_VideosAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dynashwet.chatmate.Activity.NewPublicPost;
import com.dynashwet.chatmate.Chat;
import com.dynashwet.chatmate.Dashboard.ReportUser;
import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.Model.MyModel;
import com.dynashwet.chatmate.PostComment;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.RequestHandler;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.dynashwet.chatmate.Utils.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static com.dynashwet.chatmate.R.id.image_send_friendreq;
import static com.dynashwet.chatmate.R.id.img_playback;
import static com.dynashwet.chatmate.R.id.img_vol;
import static com.dynashwet.chatmate.R.id.profile_image;


public class MyVideosAdapter extends AAH_VideosAdapter {

    private final List<MyModel> list;
    private final Picasso picasso;
    Context mCtx;
    String UID;
    String likes ="";
    boolean isClicked = false;
    static JSONArray jsonArray;

    private Mediator mediator;
    private static final int TYPE_VIDEO = 0, TYPE_TEXT = 1;

    public class MyViewHolder extends AAH_CustomViewHolder {
        final TextView tv,name,time,caption,allcomments,likescount,commentcount;
        AAH_VideoImage image;
        final ImageView img_vol, img_playback, overflow, imagehear, share, image_sent_friendReq, profile_image, send_friendReq, chat;
        //to mute/un-mute video (optional)
        boolean isMuted;

        public MyViewHolder(View x) {
            super(x);
            tv = ButterKnife.findById(x, R.id.tv);
            img_vol = ButterKnife.findById(x, R.id.img_vol);
            img_playback = ButterKnife.findById(x, R.id.img_playback);
            overflow = ButterKnife.findById(x, R.id.overflow);
            image = ButterKnife.findById(x, R.id.image);
            name = ButterKnife.findById(x, R.id.name);
            time = ButterKnife.findById(x, R.id.time);
            caption = ButterKnife.findById(x, R.id.caption);
            imagehear = ButterKnife.findById(x, R.id.image_heart);
            share = ButterKnife.findById(x, R.id.share);
            allcomments = ButterKnife.findById(x, R.id.view_all_comments);
            likescount = ButterKnife.findById(x, R.id.likes_count);
            commentcount = ButterKnife.findById(x, R.id.comment_count);
            image_sent_friendReq = ButterKnife.findById(x, R.id.image_sent_friendReq);
            profile_image = ButterKnife.findById(x, R.id.profile_image);
            send_friendReq = ButterKnife.findById(x, image_send_friendreq);
            chat = ButterKnife.findById(x, R.id.chat);

            send_friendReq.setVisibility(View.GONE);
            mediator = new Mediator(mCtx);

            try{
                SharedPreferences pref = mCtx.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                UID = pref.getString("UserID", "");
            }catch (NullPointerException ex){
                ex.printStackTrace();
            }

        }

        //override this method to get callback when video starts to play
        @Override
        public void videoStarted() {
            super.videoStarted();
            img_playback.setImageResource(R.drawable.ic_pause);
            if (isMuted) {
                unmuteVideo();
                img_vol.setImageResource(R.drawable.ic_unmute);
            } else {
                muteVideo();
                img_vol.setImageResource(R.drawable.ic_mute);
            }
        }

        @Override
        public void pauseVideo() {
            super.pauseVideo();
            img_playback.setImageResource(R.drawable.ic_play);
        }
    }


    public class MyTextViewHolder extends AAH_CustomViewHolder {
        final TextView tv,name,time,caption,allcomments,likescount,commentcount;

        final ImageView overflow, imagehear, share, image_sent_friendReq, profile_image, send_friendReq, chat;

        public MyTextViewHolder(View x) {
            super(x);
            tv = ButterKnife.findById(x, R.id.tv);
            overflow = ButterKnife.findById(x, R.id.overflow);
            name = ButterKnife.findById(x, R.id.name);
            time = ButterKnife.findById(x, R.id.time);
            caption = ButterKnife.findById(x, R.id.caption);
            imagehear = ButterKnife.findById(x, R.id.image_heart);
            share = ButterKnife.findById(x, R.id.share);
            allcomments = ButterKnife.findById(x, R.id.view_all_comments);
            likescount = ButterKnife.findById(x, R.id.likes_count);
            commentcount = ButterKnife.findById(x, R.id.comment_count);
            image_sent_friendReq = ButterKnife.findById(x, R.id.image_sent_friendReq);
            profile_image = ButterKnife.findById(x, R.id.profile_image);
            send_friendReq = ButterKnife.findById(x, R.id.image_send_friendreq);
            chat = ButterKnife.findById(x, R.id.chat);
            send_friendReq.setVisibility(View.GONE);

            mediator = new Mediator(mCtx);
            SharedPreferences pref = mCtx.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            UID = pref.getString("UserID", "");
        }
    }

    public MyVideosAdapter(List<MyModel> list_urls, Picasso p, Context mCtx) {
        this.list = list_urls;
        this.picasso = p;
        this.mCtx = mCtx;
    }

    @Override
    public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_TEXT) {
            View itemView1 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_text, parent, false);
            return new MyTextViewHolder(itemView1);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_card, parent, false);
            return new MyViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(final AAH_CustomViewHolder holder, int position) {
        holder.setLooping(true);
        if (list.get(position).getType().equals("text")) {

            if(list.get(position).getIsBlock().equals("1")){
                ((MyTextViewHolder)holder).chat.setVisibility(View.GONE);
                ((MyTextViewHolder)holder).allcomments.setVisibility(View.GONE);
            }

            ((MyTextViewHolder) holder).tv.setText(list.get(position).getName());

//            if(position == list.size()-1){//Display more posts
//                Toast.makeText(mCtx, "Reached end of List", Toast.LENGTH_SHORT).show();
//                newPublicPost.displayMorePosts();
//            }

            ((MyTextViewHolder) holder).allcomments.setOnClickListener(view ->  {

                Intent i = new Intent(mCtx.getApplicationContext(), PostComment.class);
                i.putExtra("comment", "all_comments");
                i.putExtra("isShare", list.get(position).getIsShare());
                i.putExtra("pid", list.get(position).getPid());
                mCtx.startActivity(i);
            });

            ((MyTextViewHolder)holder).chat.setOnClickListener(view ->  {

                Intent i  = new Intent(mCtx.getApplicationContext(), Chat.class);
                i.putExtra("friendid", list.get(position).getUploadby());
                i.putExtra("dp", list.get(position).getPic());
                i.putExtra("name", list.get(position).getUsername());
                mCtx.startActivity(i);

            });


            ((MyTextViewHolder) holder).caption.setText(list.get(position).getCaption());
            ((MyTextViewHolder) holder).time.setText(list.get(position).getTime());
            ((MyTextViewHolder) holder).name.setText(list.get(position).getUsername());
            ((MyTextViewHolder) holder).likescount.setText(list.get(position).getLikes());
            ((MyTextViewHolder) holder).commentcount.setText(list.get(position).getComments()+" Comments");
            Glide.with(mCtx).load(list.get(position).getPic()).into(((MyTextViewHolder) holder).profile_image);

//

            ((MyTextViewHolder) holder).overflow.setOnClickListener(view-> {

                PopupMenu popup = new PopupMenu(mCtx, ((MyTextViewHolder) holder).overflow);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_options);

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_block_user:

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.BLOCK_USER,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String msg = jsonObject.getString("Msg");
                                                    if(msg.equals("Success"))
                                                    {

                                                        if(list.get(position).getIsBlock().equals("1"))
                                                        {
                                                            Toast.makeText(mCtx, "Unblock from menu", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(mCtx, "Blocked User!", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                    else if(msg.equals("Update")){

                                                        if(list.get(position).getIsBlock().equals("1"))
                                                        {
                                                            Toast.makeText(mCtx, "Unblock from menu", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {

                                                            Toast.makeText(mCtx, "You Blocked "+list.get(position).getUsername(), Toast.LENGTH_SHORT).show();


                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(mCtx, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
//                                            progressDialog.dismiss();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("Dyna-BlockedBy", UID);
                                        params.put("Dyna-BlockedUser", list.get(position).getPid());
                                        params.put("Dyna-BlockStatus", "1");
                                        return params;
                                    }
                                };

                                RequestQueue requestQueue = Volley.newRequestQueue(mCtx);
                                requestQueue.add(stringRequest);

                                break;
                            case R.id.action_report_user:
                                Intent intent = new Intent(mCtx.getApplicationContext(), ReportUser.class);
//                                    SharedPreferences  pref =mCtx.pr(Context.MODE_PRIVATE);
//                                    String PostID = pref.getString("PostID", "");

                                intent.putExtra("POSTID", list.get(position).getPid());
                                mCtx.startActivity(intent);
                                break;

                            case R.id.action_deletepost:
                                //Delete post API
                                deletePost(UID, list.get(position).getPid(), position);
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            });



            ((MyTextViewHolder) holder).share.setOnClickListener(view->{
//                Toast.makeText(mCtx, "To be integrated", Toast.LENGTH_SHORT).show();
                String text = list.get(position).getName();
//
                Intent share = new Intent(Intent.ACTION_SEND);
//                        Uri screenshotUri = Uri.parse(post_url);
                share.setType("text/*");
                share.putExtra(Intent.EXTRA_TEXT, text);

                mCtx.startActivity(Intent.createChooser(share, "Send To"));//Title of the dialog the system will open
            });

            ((MyTextViewHolder) holder).imagehear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isClicked==false){
                        LikeDisCall(UID, list.get(position).getPid(), "1");
//                       like_count.setText(liked+ " likes");
                        ((MyTextViewHolder) holder).likescount.setText(String.valueOf(Integer.parseInt(((MyTextViewHolder) holder).likescount.getText().toString().trim())+1));

                        ((MyTextViewHolder) holder).imagehear.setImageResource(R.drawable.ic_heart);
                        isClicked = true;
                    }
                    else {
                        LikeDisCall(UID, list.get(position).getPid(), "0");
//                        like_count.setText(liked + "likes");
                        ((MyTextViewHolder) holder).likescount.setText(String.valueOf(Integer.parseInt(((MyTextViewHolder) holder).likescount.getText().toString().trim())-1));

                        ((MyTextViewHolder) holder).imagehear.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        isClicked = false;
                    }

                }
            });

            ((MyTextViewHolder) holder).send_friendReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequest(list.get(position).getPid(), UID);
                    ((MyTextViewHolder) holder).image_sent_friendReq.setVisibility(View.VISIBLE);
                    ((MyTextViewHolder) holder).send_friendReq.setVisibility(View.INVISIBLE);
                }
            });

        }
        else
        {//For images and videos

            if(list.get(position).getIsLike().equals("1"))
            {
                ((MyViewHolder) holder).imagehear.setImageResource(R.drawable.ic_heart);
            }

            if(list.get(position).getIsBlock().equals("1")){
                ((MyViewHolder)holder).chat.setVisibility(View.GONE);
                ((MyViewHolder)holder).allcomments.setVisibility(View.GONE);
            }

            ((MyViewHolder) holder).tv.setText(list.get(position).getName());

//            if(position == list.size()-1){//Display more posts
//                Toast.makeText(mCtx, "Reached end of List", Toast.LENGTH_SHORT).show();
//                newPublicPost.displayMorePosts();
//            }

            //todo
            holder.setImageUrl(list.get(position).getImage_url());
//            holder.setVideoUrl(NewPublicPost.getProxy().getProxyUrl(list.get(position).getVideo_url()+""));
            holder.setVideoUrl(list.get(position).getVideo_url());

            //load image into imageview
            if (list.get(position).getImage_url() != null && !list.get(position).getImage_url().isEmpty()) {
//                picasso.load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
                Picasso.with(mCtx).load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
            }

            holder.setLooping(true); //optional - true by default

            //to play pause videos manually (optional)
            ((MyViewHolder) holder).img_playback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.isPlaying()) {
                        holder.pauseVideo();
                        holder.setPaused(true);
                    } else {
                        holder.playVideo();
                        holder.setPaused(false);
                    }
                }
            });

            ((MyViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MyViewHolder) holder).isMuted) {
                        holder.unmuteVideo();
                        ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_unmute);
                    } else {
                        holder.muteVideo();
                        ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_mute);
                    }
                    ((MyViewHolder) holder).isMuted = !((MyViewHolder) holder).isMuted;
                }
            });
            //to mute/un-mute video (optional)
            ((MyViewHolder) holder).img_vol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MyViewHolder) holder).isMuted) {
                        holder.unmuteVideo();
                        ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_unmute);
                    } else {
                        holder.muteVideo();
                        ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_mute);
                    }
                    ((MyViewHolder) holder).isMuted = !((MyViewHolder) holder).isMuted;
                }
            });


            ((MyViewHolder)holder).chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i  = new Intent(mCtx.getApplicationContext(), Chat.class);
                    i.putExtra("friendid", list.get(position).getUploadby());
                    i.putExtra("dp", list.get(position).getPic());
                    i.putExtra("name", list.get(position).getUsername());
                    mCtx.startActivity(i);
                }
            });

            ((MyViewHolder) holder).share.setOnClickListener(view->{
//                Toast.makeText(mCtx, "To be integrated", Toast.LENGTH_SHORT).show();
                String text;
                if(list.get(position).getImage_url() != null && !list.get(position).getImage_url().isEmpty()){
                    text = list.get(position).getImage_url();
                }
                else {
                    text = list.get(position).getVideo_url();
                }
//
                Intent share = new Intent(Intent.ACTION_SEND);
//                        Uri screenshotUri = Uri.parse(post_url);
                share.setType("text/*");
                share.putExtra(Intent.EXTRA_TEXT, text);

                mCtx.startActivity(Intent.createChooser(share, "Send To"));//Title of the dialog the system will open
            });


            ((MyViewHolder) holder).allcomments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mCtx.getApplicationContext(), PostComment.class);
                    i.putExtra("comment", "all_comments");
                    i.putExtra("isShare", list.get(position).getIsShare());
                    i.putExtra("pid", list.get(position).getPid());
                    mCtx.startActivity(i);
                }
            });


            ((MyViewHolder) holder).caption.setText(list.get(position).getCaption());
            ((MyViewHolder) holder).name.setText(list.get(position).getUsername());
            ((MyViewHolder) holder).time.setText(list.get(position).getTime());
            ((MyViewHolder) holder).likescount.setText(list.get(position).getLikes());
            ((MyViewHolder) holder).commentcount.setText(list.get(position).getComments()+" Comments");
            Glide.with(mCtx).load(list.get(position).getPic()).into(((MyViewHolder) holder).profile_image);


            ((MyViewHolder) holder).overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mCtx, ((MyViewHolder) holder).overflow);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_options);

                    //adding click
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_block_user:

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.BLOCK_USER,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        String msg = jsonObject.getString("Msg");
                                                        if(msg.equals("Success"))
                                                        {

                                                            if(list.get(position).getIsBlock().equals("1"))
                                                            {
                                                                Toast.makeText(mCtx, "Unblock from menu", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(mCtx, "Blocked User!", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                        else if(msg.equals("Update")){

                                                            if(list.get(position).getIsBlock().equals("1"))
                                                            {
                                                                Toast.makeText(mCtx, "Unblock from menu", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else
                                                            {

                                                                Toast.makeText(mCtx, "You Blocked "+list.get(position).getUsername(), Toast.LENGTH_SHORT).show();


                                                            }
                                                        }
                                                        else {
                                                            Toast.makeText(mCtx, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
//                                            progressDialog.dismiss();
                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("Dyna-BlockedBy", UID);
                                            params.put("Dyna-BlockedUser", list.get(position).getUploadby());
                                            params.put("Dyna-BlockStatus", "1");
                                            return params;
                                        }
                                    };

                                    RequestQueue requestQueue = Volley.newRequestQueue(mCtx);
                                    requestQueue.add(stringRequest);
                                    break;


                                case R.id.action_report_user:
                                    Intent intent = new Intent(mCtx.getApplicationContext(), ReportUser.class);

                                    intent.putExtra("POSTID", list.get(position).getPid());
                                    mCtx.startActivity(intent);
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });


            ((MyViewHolder) holder).imagehear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isClicked==false){
                        LikeDisCall(UID, list.get(position).getPid(), "1");
                        ((MyViewHolder) holder).likescount.setText(String.valueOf(Integer.parseInt(((MyViewHolder) holder).likescount.getText().toString().trim())+1));
                        ((MyViewHolder) holder).imagehear.setImageResource(R.drawable.ic_heart);
                        isClicked = true;
                    }
                    else {
                        LikeDisCall(UID, list.get(position).getPid(), "0");
                        ((MyViewHolder) holder).likescount.setText(String.valueOf(Integer.parseInt(((MyViewHolder) holder).likescount.getText().toString().trim())-1));
                        ((MyViewHolder) holder).imagehear.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        isClicked = false;
                    }

                }
            });


            ((MyViewHolder) holder).send_friendReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequest(list.get(position).getPid(), UID);
                    ((MyViewHolder) holder).image_sent_friendReq.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).send_friendReq.setVisibility(View.INVISIBLE);
                }
            });


            if (list.get(position).getVideo_url() == null)
            {
                ((MyViewHolder) holder).img_vol.setVisibility(View.GONE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.GONE);
            } else {
                ((MyViewHolder) holder).img_vol.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.VISIBLE);
            }
        }
    }

    private void deletePost(String uid, String pid, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, AppConstant.DeletePost,
                success-> {
                    try {
                        JSONObject object = new JSONObject(success);
                        String msg = object.getString("Msg");
                        if(msg.equals("Success")){
                            list.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mCtx, "Deleted Post", Toast.LENGTH_SHORT).show();
                        }
                        else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID",uid);
                params.put("Dyna-PostID",pid);
                return params;
            }
        };
        RequestHandler.getInstance(mCtx).addToRequestQueue(request);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getType().equals("text")) {
            return TYPE_TEXT;
        } else return TYPE_VIDEO;
    }

    private void LikeDisCall(String UID, String Post_ID, String state){

        mediator.LikeDisCon(UID, Post_ID, state, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("Msg");

                if (msg.equals("SUCCESS")){
//                    Toast.makeText(getActivity(), "Liked", Toast.LENGTH_SHORT).show();
                    jsonArray = jsonObject.getJSONArray("PostDetail");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        likes = jsonObject1.getString("PostLike");
                    }
                }
            }
        });
//        return likes;

    }

    //Working
    public void sendFriendRequest(String PostID, String UID){
        mediator.sendFriendRequest(PostID, UID, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("Msg");
                if(msg.equals("SUCCESS")){
                    Toast.makeText(mCtx.getApplicationContext(),"Sent friend request" ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}