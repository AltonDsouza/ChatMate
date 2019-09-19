package com.dynashwet.chatmate.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
import com.dynashwet.chatmate.Dashboard.EnlargedView;
import com.dynashwet.chatmate.Dashboard.ReportUser;
import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.Model.MyModel;
import com.dynashwet.chatmate.NavigationDashboard.ProfileActivity;
import com.dynashwet.chatmate.PostComment;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.dynashwet.chatmate.Utils.MyApplication;
import com.dynashwet.chatmate.Utils.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.dynashwet.chatmate.R.id.image_send_friendreq;


public class VideosAdapterPublic extends AAH_VideosAdapter {

    private final List<MyModel> list;
    private final Picasso picasso;
    DownloadManager mDownloadManager;
    Context mCtx;
    String UID;
    String likes ="";
    ProgressBar progressBar;
    String fragment;
    boolean isClicked = false;
    static JSONArray jsonArray;

    private Mediator mediator;
    private static final int TYPE_VIDEO = 0, TYPE_TEXT = 1;

    public class MyViewHolder extends AAH_CustomViewHolder {
        final TextView tv,name,time,caption,allcomments,likescount,commentcount, PID;

        final ImageView img_vol,download, img_playback, overflow, imagehear, share, image_sent_friendReq, profile_image, send_friendReq, chat;
        final LinearLayout theOneWhoGoesToProfile;
        final ProgressBar progressBar;
        AAH_VideoImage image;
        //to mute/un-mute video (optional)
        boolean isMuted;

        public MyViewHolder(View x) {
            super(x);
            tv = ButterKnife.findById(x, R.id.tv);
            img_vol = ButterKnife.findById(x, R.id.img_vol);
            download = ButterKnife.findById(x, R.id.download);
            image = ButterKnife.findById(x, R.id.image);
            img_playback = ButterKnife.findById(x, R.id.img_playback);
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
            send_friendReq = ButterKnife.findById(x, image_send_friendreq);
            chat = ButterKnife.findById(x, R.id.chat);
            theOneWhoGoesToProfile = ButterKnife.findById(x, R.id.theOneWhoGoesToProfile);
            progressBar = ButterKnife.findById(x, R.id.progressBar);
            chat.setVisibility(View.GONE);
            PID = ButterKnife.findById(x, R.id.PID);
            mediator = new Mediator(mCtx);

            try{
                SharedPreferences pref = mCtx.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                UID = pref.getString("UserID", "");
            }catch (NullPointerException ex){
                ex.printStackTrace();
            }


//            if(fragment.equals("FriendsFragment")){
//                chat.setVisibility(View.VISIBLE);
//                send_friendReq.setVisibility(View.GONE);
//            }

//            else if(fragment.equals("ProfileActivity")){
//                send_friendReq.setVisibility(View.GONE);
//            }

        }

        //override this method to get callback when video starts to play
        @Override
        public void videoStarted() {
            super.videoStarted();
            progressBar.setVisibility(View.GONE);
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
        public void pauseVideo()
        {
            super.pauseVideo();
            img_playback.setImageResource(R.drawable.ic_play);
        }
    }


    public class MyTextViewHolder extends AAH_CustomViewHolder {
        final TextView tv,name,time,caption,allcomments,likescount,commentcount, PID;

        final ImageView overflow, imagehear, share, image_sent_friendReq, profile_image, send_friendReq, chat;

        final LinearLayout theOneWhoGoesToProfile;

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
            PID = ButterKnife.findById(x, R.id.PID);
            chat = ButterKnife.findById(x, R.id.chat);
            chat.setVisibility(View.GONE);

            theOneWhoGoesToProfile = ButterKnife.findById(x, R.id.theOneWhoGoesToProfile);

            mediator = new Mediator(mCtx);
            SharedPreferences pref = mCtx.getSharedPreferences("MyPref", Context.MODE_PRIVATE);

//            SharedPreferences preferences = PreferenceManager
//                    .getDefaultSharedPreferences(mCtx);
            UID = pref.getString("UserID", "");

        }
    }


    public VideosAdapterPublic(List<MyModel> list_urls, Picasso p, Context mCtx) {
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
            ((MyTextViewHolder) holder).tv.setText(list.get(position).getName());

            if(list.get(position).getIsBlock().equals("1")){
                ((MyViewHolder)holder).allcomments.setVisibility(View.GONE);
            }

//            if(list.get(position).getIsLike().equals("1"))
//            {
//                ((MyTextViewHolder) holder).imagehear.setImageResource(R.drawable.ic_heart);
//            }
//            if(position == list.size()-1){//Display more posts
//                Toast.makeText(mCtx, "Reached end of List", Toast.LENGTH_SHORT).show();
//                newPublicPost.displayMorePosts();
//            }
            if(list.get(position).getIsLike().equals("1"))
            {
                ((MyTextViewHolder) holder).imagehear.setImageResource(R.drawable.ic_heart);
            }

            if(list.get(position).getIsFriend().equals("Yes"))
            {
                ((MyTextViewHolder) holder).send_friendReq.setVisibility(View.GONE);
            }

            ((MyTextViewHolder) holder).allcomments.setOnClickListener(view ->  {

                Intent i = new Intent(mCtx.getApplicationContext(), PostComment.class);
                i.putExtra("comment", "all_comments");
                i.putExtra("isShare", list.get(position).getIsShare());
                i.putExtra("pid", list.get(position).getPid());
                mCtx.startActivity(i);
            });




            ((MyTextViewHolder) holder).caption.setText(list.get(position).getCaption());
            ((MyTextViewHolder) holder).time.setText(list.get(position).getTime());
            ((MyTextViewHolder) holder).name.setText(list.get(position).getUsername());
            ((MyTextViewHolder) holder).likescount.setText(list.get(position).getLikes());
            ((MyTextViewHolder) holder).commentcount.setText(list.get(position).getComments()+" Comments");
            Glide.with(mCtx).load(list.get(position).getPic()).into(((MyTextViewHolder) holder).profile_image);
            if(list.get(position).getUploadby().equals(UID))
            {
                ((MyTextViewHolder) holder).send_friendReq.setVisibility(View.GONE);
            }

//


            ((MyTextViewHolder) holder).overflow.setOnClickListener(view-> {

                PopupMenu popup = new PopupMenu(mCtx, ((MyTextViewHolder) holder).overflow);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_options);
                    popup.getMenu().findItem(R.id.action_deletepost).setVisible(true);
                    popup.getMenu().findItem(R.id.action_report_user).setVisible(false);
                    popup.getMenu().findItem(R.id.action_block_user).setVisible(false);


//                if(list.get(position).getIsBlock().equals("1")){
//                    popup.getMenu().findItem(R.id.action_deletepost).setTitle("Unblock");
//                }

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
                                                        Toast.makeText(mCtx, ""+list.get(position).getIsBlock(), Toast.LENGTH_SHORT).show();

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
                                    public void onErrorResponse(VolleyError error)
                                    {
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
//                        ((MyTextViewHolder) holder).likescount.setText(String.valueOf(Integer.parseInt((String) ((MyTextViewHolder) holder).likescount.getText())+ 1));
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


            ((MyTextViewHolder) holder).profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i  = new Intent(mCtx.getApplicationContext(), EnlargedView.class);
                    i.putExtra("post_image", list.get(position).getPic());
                    mCtx.startActivity(i);
                }
            });


            ((MyTextViewHolder) holder).theOneWhoGoesToProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i  = new Intent(mCtx.getApplicationContext(), ProfileActivity.class);
                    i.putExtra("UIDcomingFromPublic", list.get(position).getUploadby());
                    mCtx.startActivity(i);
                }
            });



        }
        else
        {//For images and videos
            if(list.get(position).getIsBlock().equals("1")){
                ((MyViewHolder)holder).allcomments.setVisibility(View.GONE);
            }

            if(list.get(position).getIsLike().equals("1"))
            {
                ((MyViewHolder) holder).imagehear.setImageResource(R.drawable.ic_heart);
            }

            if(list.get(position).getIsFriend().equals("Yes"))
            {
                ((MyViewHolder) holder).send_friendReq.setVisibility(View.GONE);
            }

            if (list.get(position).getType().equals("video"))
            {

                ((MyViewHolder) holder).download.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
            }

            ((MyViewHolder) holder).download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    download();

                    String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            + File.separator + "Scroll_Status";

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(list.get(position).getVideo_url()))
                            .setTitle(list.get(position).getPid())
                            .setDescription("Downloading")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationUri(Uri.fromFile(new File(rootDir)))
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true);

                    DownloadManager dm = (DownloadManager) mCtx.getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
//                    long downloadId = mDownloadManager.enqueue(request);
                }
            });

            ((MyViewHolder) holder).tv.setText(list.get(position).getName());

//            if(position == list.size()-1){//Display more posts
//                Toast.makeText(mCtx, "Reached end of List", Toast.LENGTH_SHORT).show();
//                newPublicPost.displayMorePosts();
//            }

            holder.setImageUrl(list.get(position).getImage_url());
//            holder.setVideoUrl(NewPublicPost.getProxy().getProxyUrl(list.get(position).getVideo_url()+""));
            holder.setVideoUrl(list.get(position).getVideo_url());
//            holder.setVideoUrl(MyApplication.getProxy().getProxyUrl(list.get(position).getVideo_url()+""));
//            holder.setVideoUrl(MyApplication.getProxy().getProxyUrl
//                    (list.get(position).getVideo_url()+"")); // url should not be null

            //load image into imageview


            if(list.get(position).getUploadby().equals(UID))
            {
                ((MyViewHolder) holder).send_friendReq.setVisibility(View.GONE);
            }
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

            //to mute/un-mute video (optional)
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

            ((MyViewHolder) holder).share.setOnClickListener(view->{
//                Toast.makeText(mCtx, "To be integrated", Toast.LENGTH_SHORT).show();
                String text;
                if(list.get(position).getVideo_url()==null){
                    text = list.get(position).getImage_url();
                }
                else {
                    text = list.get(position).getVideo_url();//Video needs to be fixed
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

            try{
                Glide.with(mCtx).load(list.get(position).getPic()).into(((MyViewHolder) holder).profile_image);

            }catch (NullPointerException ex){
                ex.printStackTrace();
            }


            ((MyViewHolder) holder).overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mCtx, ((MyViewHolder) holder).overflow);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_options);




                    if(list.get(position).getUploadby().equals(UID))
                    {
                        popup.getMenu().findItem(R.id.action_deletepost).setVisible(true);
                        popup.getMenu().findItem(R.id.action_report_user).setVisible(false);
                        popup.getMenu().findItem(R.id.action_block_user).setVisible(false);
                    }


//                    if(list.get(position).getIsBlock().equals("1")){
//                        popup.getMenu().findItem(R.id.action_deletepost).setTitle("Unblock");
//                    }
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
                                                            Toast.makeText(mCtx, ""+list.get(position).getIsBlock(), Toast.LENGTH_SHORT).show();


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
//

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
                }
            });


            ((MyViewHolder) holder).imagehear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isClicked==false){
                        LikeDisCall(UID, list.get(position).getPid(), "1");
                        ((MyViewHolder) holder).likescount.setText(String.valueOf(Integer.parseInt(((MyViewHolder) holder).likescount.getText().toString().trim())+1));

//                        ((MyViewHolder) holder).likescount.setText(Integer.parseInt((String.valueOf(((MyViewHolder) holder).likescount.getText())).replace(" likes",""))+1);
                        ((MyViewHolder) holder).imagehear.setImageResource(R.drawable.ic_heart);
                        isClicked = true;
                    }
                    else {
                        LikeDisCall(UID, list.get(position).getPid(), "0");
                        ((MyViewHolder) holder).likescount.setText(String.valueOf(Integer.parseInt(((MyViewHolder) holder).likescount.getText().toString().trim())-1));

//                        ((MyViewHolder) holder).likescount.setText(Integer.parseInt(String.valueOf(((MyViewHolder) holder).likescount.getText()))-1);
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

            ((MyViewHolder) holder).profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i  = new Intent(mCtx.getApplicationContext(), EnlargedView.class);
                    i.putExtra("post_image", list.get(position).getPic());
                    mCtx.startActivity(i);
                }
            });


            ((MyViewHolder) holder).theOneWhoGoesToProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i  = new Intent(mCtx.getApplicationContext(), ProfileActivity.class);
                    i.putExtra("UIDcomingFromPublic", list.get(position).getUploadby());
                    mCtx.startActivity(i);
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
        RequestQueue requestQueue = Volley.newRequestQueue(mCtx);
        requestQueue.add(request);
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

    public void newdownloads()
    {


    }


}