package com.dynashwet.chatmate.NavigationDashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dynashwet.chatmate.Adapter.MyVideosAdapter;
import com.dynashwet.chatmate.Adapter.ProfileAdapter;
import com.dynashwet.chatmate.Adapter.ProfileGridAdapter;
import com.dynashwet.chatmate.Adapter.UserProfileAdapter;
import com.dynashwet.chatmate.Adapter.VideosAdapterPublic;
import com.dynashwet.chatmate.Dashboard.CommentsActivity;
import com.dynashwet.chatmate.Dashboard.EditProfile;
import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.Model.MyModel;
import com.dynashwet.chatmate.Models.Public;
import com.dynashwet.chatmate.Models.profilegrid;
import com.dynashwet.chatmate.PostComment;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.RequestHandler;
import com.dynashwet.chatmate.Settings.MyPosts;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.dynashwet.chatmate.Utils.AppConstant.BASE_URL;
import static com.dynashwet.chatmate.Utils.AppConstant.getFriendRequests;

/*
Author:    Alton Dsouza
 */
public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.rv_profile)

    AAH_CustomRecyclerView recyclerView;
    private CircleImageView pro_image;
    private TextView name, phoneNo,post,friend, reward;
    private String UID, postID;
    private ProgressDialog  progressDialog;
    private List<MyModel> paginatedList = new ArrayList<>();
    private Mediator mediator;
    private RelativeLayout layoutThatHoldsProfileDetails;

    private final List<MyModel> modelList = new ArrayList<>();
//    private PostAdapter publicAdapter;
    private static final String TAG = "ProfileActivity";
    private String URL = "";
    private boolean showMenu = false;

    private static int mResults;
    ListView listView;
    //    private List<profilegrid> lstAnime;
    private boolean _hasLoadedOnce= false; // your boolean field
    RequestOptions option;
    private boolean comingFromPublic = false;
    ProfileAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        Log.d(TAG,"onCreate: ProfileActivity started");

        post=findViewById(R.id.post);
        friend=findViewById(R.id.friend);
        reward=findViewById(R.id.reward);
        pro_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        layoutThatHoldsProfileDetails = findViewById(R.id.proDetails);
//        phoneNo = findViewById(R.id.profile_number);
        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.rv_profile);
        option=new RequestOptions().fitCenter().placeholder(R.drawable.defaultcomment).error(R.drawable.defaultcomment);

        mediator = new Mediator(this);
//        listView = findViewById(R.id.myPosts);
//        lstAnime = new ArrayList<>() ;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//        UID = pref.getString("UserID", "");

        //If coming from Search Fragment
        if(getIntent().getStringExtra("UIDFromSearch")!=null){
            UID = getIntent().getStringExtra("UIDFromSearch");
            loadProfileDetails(UID);
            comingFromPublic = true;
            URL = AppConstant.GetMyPosts;

        }

        //If coming from Notification Fragment
        else if(getIntent().getStringExtra("pidComingFromNotification")!=null){
            UID = getIntent().getStringExtra("UIDcomingFromNotification");
            postID = getIntent().getStringExtra("pidComingFromNotification");
            layoutThatHoldsProfileDetails.setVisibility(View.GONE);

            URL = AppConstant.GetNotiPost;

        }

        else if(getIntent().getStringExtra("UIDFriendRequest")!=null){
            UID = getIntent().getStringExtra("UIDFriendRequest");
            loadProfileDetails(UID);

            URL = AppConstant.GetMyPosts;
        }

        else if(getIntent().getStringExtra("UIDcomingFromPublic")!=null){
            UID = getIntent().getStringExtra("UIDcomingFromPublic");
            comingFromPublic = true;

            loadProfileDetails(UID);
            URL = AppConstant.GetMyPosts;
        }

        else {
            UID = pref.getString("UserID", "");
            loadProfileDetails(UID);
            showMenu = true;
            reward.setVisibility(View.VISIBLE);

            URL = AppConstant.GetMyPosts;
        }

        LoadPosts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(showMenu){
            inflater.inflate(R.menu.menu_home, menu);
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.actionEditProfile){
            startActivity(new Intent(getApplicationContext(), EditProfile.class));
        }
        return false;
    }

    private void LoadPosts() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
//                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                Log.e(TAG, "onLoadAPI: SUCCESS");
                                Picasso p = Picasso.with(getApplicationContext());
//                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = jsonObject.getJSONArray("Result");

                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String upload_by="", post_type ="", caption = "", name = "", upload_time = "",
                                            pid = "", pro_pic = "", post_image = "", isLike = "", likes = "", isShare = "",
                                            commCount="", isFriend = "", isBlock="";
                                    JSONObject jsonArray1 = jsonObject1.getJSONObject("Info");
                                    for(int i1 = 0; i1<jsonArray1.length(); i1++){
//                                 JSONObject jsonObject2 = jsonArray1.getJSONObject(i1);
                                        upload_by = jsonArray1.getString("UploadBy");
                                        post_type = jsonArray1.getString("PostType");
                                        likes = jsonArray1.getString("PostLike");
                                        commCount = jsonArray1.getString("CommCount");
                                        caption = jsonArray1.getString("PostCaption");
                                        name = jsonArray1.getString("Name");
                                        upload_time = jsonArray1.getString("UploadAt");
                                        pid = jsonArray1.getString("PID");
                                        isBlock = jsonArray1.getString("IsBlock");
                                        isShare = jsonArray1.getString("IsShare");
                                        isFriend = jsonArray1.getString("IsFriend");

                                        pro_pic = AppConstant.BASE_URL+jsonArray1.getString("Pic");//Profile Image

                                        isLike = jsonArray1.getString("LikeState");
                                    }


                                    if(post_type.equals("image")){
                                        post_image = AppConstant.BASE_URL+jsonArray1.getString("PostPath");//Post Image
                                        modelList.add(new MyModel(post_image,
                                                caption,
                                                post_type,
                                                isShare,
                                                pid,
                                                caption,
                                                pro_pic,
                                                name,
                                                likes,
                                                isLike,
                                                commCount,
                                                upload_by,
                                                upload_time,
                                                isFriend,
                                                isBlock));
                                    }
                                    else if(post_type.equals("text")){
                                        post_image = jsonArray1.getString("PostPath");//Post Text
                                        modelList.add(new MyModel(post_image,
                                                post_type,
                                                isShare,
                                                pid,
                                                caption,
                                                pro_pic,
                                                name,
                                                likes,
                                                isLike,
                                                commCount,
                                                upload_by,
                                                upload_time,
                                                isFriend,
                                                isBlock));
                                    }
                                    else if(post_type.equals("video")){
                                        post_image = AppConstant.BASE_URL+jsonArray1.getString("PostPath");//Post Video
                                         modelList.add(new MyModel(post_image,
                                    "http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70,so_0/v1481795675/1_pyn1fm.jpg",
                                    caption,
                                    post_type,
                                    isShare,
                                    pid,
                                    caption,
                                    pro_pic,
                                    name,
                                    likes,
                                    isLike,
                                    commCount,
                                    upload_by,
                                    upload_time,
                                    isFriend,
                                     isBlock));
                                    }
                                    //String name, String caption, String profile_image, String image, String post_type, String post_id, String upload_time
                                }

//                                int iterations = modelList.size();
//
//                                if(iterations > 10){
//                                    iterations = 10;
//                                }
//                                mResults = 10;
//                                for(int i = 0; i < iterations; i++){
//                                    paginatedList.add(modelList.get(i));
//                                }

//                                MyVideosAdapter mAdapter = new MyVideosAdapter(modelList, p,TAG, ProfileActivity.this);
                                UserProfileAdapter mAdapter = new UserProfileAdapter(modelList, p,getApplicationContext(), comingFromPublic);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());

                                //todo before setAdapter
                                recyclerView.setActivity(ProfileActivity.this);

                                //optional - to play only first visible video
                                recyclerView.setPlayOnlyFirstVideo(true); // false by default

                                //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
                                recyclerView.setCheckForMp4(false); //true by default

                                //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
                                recyclerView.setDownloadPath(Environment.getExternalStorageDirectory() + "/.MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

                                recyclerView.setDownloadVideos(true); // false by default

                                recyclerView.setVisiblePercent(50); // percentage of View that needs to be visible to start playing

                                //extra - start downloading all videos in background before loading RecyclerView
                                List<String> urls = new ArrayList<>();
                                for (MyModel object : modelList) {
                                    if (object.getVideo_url() != null && object.getVideo_url().contains("http"))
                                        urls.add(object.getVideo_url());
                                }
                                try
                                {
                                    recyclerView.preDownload(urls);
                                }
                                catch (NullPointerException e)
                                {

                                }


                                recyclerView.setAdapter(mAdapter);
                                //call this functions when u want to start autoplay on loading async lists (eg firebase)
                                recyclerView.smoothScrollBy(0,1);
                                recyclerView.smoothScrollBy(0,-1);

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "No Posts to be displayed!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
               mediator.errorStuff(error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Dyna-UserID", UID);
                    //If postID exists and coming from Notification Fragment
                    if(URL.equals(AppConstant.GetNotiPost)){
                        params.put("Dyna-PostID", postID);
                    }
                    return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
//        loadProfileDetails(UID);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    private void loadProfileDetails(final String UID){
//        progressDialog.setMessage("Loading...");
////        progressDialog.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.FETCH_PROFILE_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                JSONArray  jsonArray = jsonObject.getJSONArray("result");
                                for(int i = 0; i<1; i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    name.setText(jsonObject1.getString("Name"));
                                    friend.setText(jsonObject1.getString("Friends")+"\nFRIENDS");
                                    post.setText(jsonObject1.getString("Posts")+"\nPOST");
//                                    phoneNo.setText(jsonObject1.getString("Contact"));
//                                    Picasso.with(getApplicationContext()).load(BASE_URL+
//                                            jsonObject1.getString("ProfilePic")).into(pro_image);

                                    Glide.with(getApplicationContext()).load(BASE_URL+
                                            jsonObject1.getString("ProfilePic")).apply(option).into(pro_image);

                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("Profile_Pic", BASE_URL+
                                            jsonObject1.getString("ProfilePic"));
                                    editor.putString("Name", jsonObject1.getString("Name"));
                                    editor.putString("contact", jsonObject1.getString("Contact"));
                                    editor.commit();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID", UID);
                return params;
            }
        };

       RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

//    public class PostAdapter extends BaseAdapter {
//        private List<Public> publics;
//        private Context context;
//
//        public PostAdapter(List<Public> publics, Context context) {
//            this.publics = publics;
//            this.context = context;
//        }
//
//        @Override
//        public int getCount() {
//            return publics.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return publics.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup viewGroup) {
//            view = LayoutInflater.from(context).inflate(R.layout.fragment_public_listitem, viewGroup, false);
//            CircleImageView circleImageView = view.findViewById(R.id.profile_image);
//            TextView name = view.findViewById(R.id.name);
//            TextView caption = view.findViewById(R.id.caption);
//            TextView post_text = view.findViewById(R.id.post_text);
//            TextView all_comments = view.findViewById(R.id.view_all_comments);
//            TextView like_count = view.findViewById(R.id.likes_count);
//            TextView pid = view.findViewById(R.id.PID);
//
//            ImageView overflow = view.findViewById(R.id.overflow);
//            ImageView carouselView = view.findViewById(R.id.carouselView);
////            TextView comment = view.findViewById(R.id.comment_count);
//            ImageView comment = view.findViewById(R.id.comment);
//            ImageView friend_req = view.findViewById(R.id.image_send_friendreq);
//            ImageView share = view.findViewById(R.id.share);
//            ImageView like = view.findViewById(R.id.image_heart);
//
//            VideoView videoView = view.findViewById(R.id.post_video);
//
//            if(publics.get(position).getPost_type().equals("image")){
//                Picasso.with(context).load(publics.get(position).getImage()).into(carouselView);
//            }//Image
//            else if(publics.get(position).getPost_type().equals("text")){
//                carouselView.setVisibility(View.GONE);
//                post_text.setVisibility(View.VISIBLE);
//                post_text.setText(publics.get(position).getImage());
//            }//Text
//            else {
//                carouselView.setVisibility(View.GONE);
//                videoView.setVisibility(View.VISIBLE);
//                //SetVideo
//            }//Video
//
//            Picasso.with(context).load(publics.get(position).getPro_image()).into(circleImageView);
//            name.setText(publics.get(position).getName());
//            caption.setText(publics.get(position).getCaption());
////            like_count.setText(publics.get(position).getLikes());
//            pid.setText(publics.get(position).getPost_ID());
//            friend_req.setVisibility(View.GONE);
//            like.setVisibility(View.GONE);
////            comment.setVisibility(View.GONE);
//            all_comments.setVisibility(View.GONE);
//            share.setVisibility(View.GONE);
//            comment.setVisibility(View.GONE);
////            comment.setVisibility(View.GONE);
////            comment.setText(publics.get(position).getCommCount());
//
//            all_comments.setOnClickListener(view1 -> {
//                //View All Comments
//                Intent i = new Intent(getApplicationContext(), PostComment.class);
////                i.putExtra("comment", "all_comments");
//                i.putExtra("pid", pid.getText().toString());
//                startActivity(i);
//            });
//
//            share.setOnClickListener(view1 -> {
////                Toast.makeText(getApplicationContext(), "Work in progress", Toast.LENGTH_SHORT).show();
//                //Ridirect to NextActivty here
//            });
//
//            overflow.setOnClickListener(view1 -> {
////                showPopUp(overflow);
//            });
//            return view;
//        }
//    }
//    private void setuprecyclerview(List<profilegrid> lstAnime) {
//
//        ProfileGridAdapter myadapter = new ProfileGridAdapter(getApplicationContext(),lstAnime) ;
//        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
//        recyclerView.setAdapter(myadapter);
//
//
//    }
}


