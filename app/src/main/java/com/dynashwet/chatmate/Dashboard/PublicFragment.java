package com.dynashwet.chatmate.Dashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.dynashwet.chatmate.FullScreenMediaController;
import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.Models.Public;
import com.dynashwet.chatmate.PostComment;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Upload.GalleryActivity;
import com.dynashwet.chatmate.Upload.PhotoActivity;
import com.dynashwet.chatmate.Upload.VideoActivity;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.dynashwet.chatmate.Utils.VolleyCallBack;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicFragment extends Fragment implements View.OnClickListener {

    private static  final String TAG = "NewPublicPost";
    private List<Public> publics = new ArrayList<>() ;
    private List<Public> paginatedList = new ArrayList<>();
    private Mediator mediator;
    private PublicAdapter adapter;
    static String likes = "";
    static JSONArray jsonArray;
    private ProgressDialog progressDialog;
    private String UID;
//    private String [] UploadBys;
//    private EditText editText;
    private static String url [];
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private static int mResults;
    boolean _hasLoadedOnce = false;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MediaController mediaController;
    private boolean loading = true;
//    int pastVisiblesItems, visibleItemCount, totalItemCount;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public, container, false);
        Log.i(TAG, "onCreate: Started Public Fragment !");
        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.upload);
//        editText = (EditText)view.findViewById(R.id.postown);
//        editText.setOnClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mediator = new Mediator(getActivity());


        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        floatingActionButton.setOnClickListener(this);
        NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScroll);
//        post = (Button)view.findViewById(R.id.post);

//        post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
        recyclerView = (RecyclerView) view
                .findViewById(R.id.publicList);
//        final LinearLayoutManager



        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if(v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                scrollY > oldScrollY) {
                            //code to fetch more data for endless scrolling
//                    Log.e(TAG, "Reached end of list!");
                            // displayMorePosts();
                        }
                    }
                });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(publics.size()>0){
                    publics.clear();
                }
                loadData(UID);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");
        // loadDummyData();
        loadData(UID);
        setHasOptionsMenu(true);
        return view;
    }

//    private void loadData(){
//        mediator.loadData(UID, new VolleyCallBack() {
//            @Override
//            public void onSuccess(String result) throws JSONException {
//
//            }
//        });
//    }

    public void loadData(final String UID){
//        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.PUBLIC_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                Log.e(TAG, "onLoadAPI: SUCCESS");
//                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = jsonObject.getJSONArray("Result");
//                    if(publics!=null){
//                        publics.clear();
//                    }
                                if(paginatedList.size()!=0 || url!=null){
                                    paginatedList.clear();
                                    url = null;
                                }
                                url = new String[jsonArray.length()];//Faced Error when placed inside for loop
//                                UploadBys = new String[jsonArray.length()];

                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String upload_by="", post_type ="", caption = "", name = "", upload_time = "",
                                            pid = "", pro_pic = "", post_image = "", isLike = "", likes = "", isShare = "", commCount = "", isFriend="";

                                    JSONObject jsonArray1 = jsonObject1.getJSONObject("Info");
                                    for(int i1 = 0; i1<jsonArray1.length(); i1++){
//                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i1);
                                       // Toast.makeText(getContext(),UID,Toast.LENGTH_LONG).show();
                                        upload_by = jsonArray1.getString("UploadBy");
                                        post_type = jsonArray1.getString("PostType");
//                                        if(!post_type.equals("video"))
                                         likes = jsonArray1.getString("PostLike");
                                            caption = jsonArray1.getString("PostCaption");
                                            name = jsonArray1.getString("Name");
                                            upload_time = jsonArray1.getString("UploadAt");
                                            pid = jsonArray1.getString("PID");
                                            commCount = jsonArray1.getString("CommCount");
                                            isFriend = jsonArray1.getString("IsFriend");
                                            isShare = jsonArray1.getString("IsShare");
                                            pro_pic = AppConstant.BASE_URL+jsonArray1.getString("Pic");//Profile Image
                                            if(post_type.equals("image")){
                                                post_image = AppConstant.BASE_URL+jsonArray1.getString("PostPath");//Post Image
                                            }
                                            else if(post_type.equals("text")){
                                                post_image =jsonArray1.getString("PostPath");//Post Text
                                            }
                                            else if(post_type.equals("video")){
                                                post_image = AppConstant.BASE_URL+jsonArray1.getString("PostPath");//Post Video
                                            }
                                            url[i] = post_image;
                                            isLike = jsonArray1.getString("LikeState");
//                                        }

//                                        UploadBys[i1] = jsonArray1.getString("UploadBy");

                                    }
                                    publics.add(new Public(name, caption, pro_pic, post_image, post_type,
                                            pid, upload_time, isLike, upload_by, likes, isFriend, commCount, isShare));

                                    //String name, String caption, String profile_image, String image, String post_type, String post_id, String upload_time
                                }

                                int iterations = publics.size();

                                if(iterations > 10){
                                    iterations = 10;
                                }
                                mResults = 10;
                                for(int i = 0; i < iterations; i++){
                                    paginatedList.add(publics.get(i));
                                }

                                adapter = new PublicAdapter(paginatedList);
                                recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                            }
                            else {
                                Toast.makeText(getActivity(), "No Posts to be displayed!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Connection timeout", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getActivity(), "server couldn't find the authenticated request", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getActivity(), "Server is not responding", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getActivity(), "Your device is not connected to internet", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getActivity(), "Parse Error (because of invalid json or xml)", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Dyna-UserID", UID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
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
//                    aPublic.setLikes(likes);
                }
            }
        });
//        return likes;

    }

    public void sendFriendRequest(String PostID, String UID){
        mediator.sendFriendRequest(PostID, UID, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("Msg");
                if(msg.equals("SUCCESS")){
                    Toast.makeText(getActivity().getApplicationContext(),"Sent friend request" ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void blockUser(final String UID, final String UploadBy, final String BlockedStatus){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.BLOCK_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                Toast.makeText(getActivity(), "Blocked User!", Toast.LENGTH_SHORT).show();
                            }
                            else if(msg.equals("Update")){
                                Toast.makeText(getContext(), "User Already blocked.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-BlockedBy", UID);
//                params.put("pid", BlockedUser);
                params.put("Dyna-BlockedUser", UploadBy);
                params.put("Dyna-BlockStatus", BlockedStatus);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void displayMorePosts(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(publics.size() > mResults && publics.size() > 0){

                int iterations;
                if(publics.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = publics.size() - mResults;
                }

                //add the new photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    paginatedList.add(publics.get(i));
                }
                mResults = mResults + iterations;
                adapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

    public void showPopupMenu(View view, String pid, int position) {
        // inflate menu
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("PostID", pid);
        editor.putString("pos", String.valueOf(position));
        editor.commit();

        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }


    @Override
    public void onResume() {
        super.onResume();
//        loadData();
    }

    //    public void loadDummyData(){
//         publics = new ArrayList<Public>();
//        publics.add(new Public("Alton", R.drawable.ic_person_black_24dp, new int[]{R.drawable.index,
//                R.drawable.auto_campus_transparent, R.drawable.flower}));
//
//        publics.add(new Public("Prahal",R.drawable.ic_person_black_24dp, new int[]{}));
//
//    }

    @Override
    public void onClick(View v) {
        if(v == floatingActionButton){
//                startActivity(new Intent(getActivity(), Share.class));
            BottomSheetMenuDialog dialog = new BottomSheetBuilder(getActivity(), R.style.AppTheme_BottomSheetDialog)
                    .setMode(BottomSheetBuilder.MODE_LIST)
                    .setMenu(R.menu.menu_bottom_simple_sheet)
                    .setItemClickListener(new BottomSheetItemClickListener() {
                        @Override
                        public void onBottomSheetItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.action_gallery:
                                    startActivity(new Intent(getActivity().getApplicationContext(), GalleryActivity.class));
                                    break;
                                case R.id.action_camera:
                                    startActivity(new Intent(getActivity().getApplicationContext(), PhotoActivity.class));
                                    break;
                                case R.id.action_video:
                                    startActivity(new Intent(getActivity().getApplicationContext(), VideoActivity.class));
                                    break;
                                case R.id.actiontext:
                                    startActivity(new Intent(getActivity().getApplicationContext(), PostText.class));
                                    break;
                            }
                        }
                    })
                    .createDialog();
            dialog.show();
        }
//        else if(v == editText){
//            startActivity(new Intent(getActivity(), PostText.class));
//        }
    }

    public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_report_user:
                    Intent intent = new Intent(getActivity().getApplicationContext(), ReportUser.class);
                  SharedPreferences  pref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    String PostID = pref.getString("PostID", "");

                    intent.putExtra("POSTID", PostID);
                    startActivity(intent);
                   // Toast.makeText(getActivity(), "Reported User", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_block_user:
                    SharedPreferences  pref1 = getActivity().getPreferences(Context.MODE_PRIVATE);
                    String postID = pref1.getString("PostID", "");
//                    String pos = pref1.getString("pos", "");
                    blockUser(UID, postID, "1");
//                    blockUser(UID, UploadBys[Integer.parseInt(pos)], "1");
                   // Toast.makeText(getActivity(), "Blocked User", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }


    //Adapter
    public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.ViewHolder>{

        private List<Public> list;
        RequestOptions option;
        private MediaController mediaController;
        public PublicAdapter(List<Public> list) {
            this.list = list;
            option=new RequestOptions().fitCenter().placeholder(R.drawable.defaultcomment).error(R.drawable.defaultcomment);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
           final Public aPublic = list.get(position);
            if(aPublic.getPost_type().equals("image")){
//                int displayWidth = getActivity().getWindowManager().getDefaultDisplay().getHeight();
//                viewHolder.carouselView.getLayoutParams().height = displayWidth / 3;
                viewHolder.carouselView.setVisibility(View.VISIBLE);
                viewHolder.post_text.setVisibility(View.GONE);
                Picasso.with(getActivity()).load(aPublic.getImage()).into(viewHolder.carouselView);
            }
            else if(aPublic.getPost_type().equals("text")){
                viewHolder.carouselView.setVisibility(View.GONE);
                viewHolder.post_text.setVisibility(View.VISIBLE);
                viewHolder.post_text.setText(aPublic.getImage());
            }
            //Condition for video here
            else if(aPublic.getPost_type().equals("video")){
                viewHolder.carouselView.setVisibility(View.GONE);
                viewHolder.post_text.setVisibility(View.GONE);
                viewHolder.videoView.setVisibility(View.VISIBLE);
//                viewHolder.video_cover.setVisibility(View.VISIBLE);
                //viewHolder.videoView.setVideoPath("https://s3.ca-central-1.amazonaws.com/codingwithmitch/media/VideoPlayerRecyclerView/Sending+Data+to+a+New+Activity+with+Intent+Extras.mp4");
            }
            if(aPublic.getIsLike().equals("1")){
                viewHolder.like.setImageResource(R.drawable.ic_heart);
            }
            else if(aPublic.getIsLike().equals("0")){
                viewHolder.like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
            if(aPublic.getIsFriend().equals("Yes")){
                viewHolder.sentReq.setVisibility(View.INVISIBLE);
                viewHolder.friend_req.setVisibility(View.GONE);
            }
            else if(aPublic.getIsFriend().equals("No")){
                viewHolder.sentReq.setVisibility(View.INVISIBLE);
                viewHolder.friend_req.setVisibility(View.VISIBLE);
            }
            else {//Req
                viewHolder.friend_req.setVisibility(View.INVISIBLE);
                viewHolder.sentReq.setVisibility(View.VISIBLE);
            }
            viewHolder.like_count.setText(aPublic.getLikes()+" Likes");
            Glide.with(getContext()).load(aPublic.getPro_image()).apply(option).into(viewHolder.circleImageView);
            Picasso.with(getActivity()).load(aPublic.getPro_image()).into(viewHolder.circleImageView);
            viewHolder.name.setText(aPublic.getName());
            viewHolder.isShare.setText(aPublic.getIsShare());
            viewHolder.uploadtime.setText(aPublic.getUpload_time());
            viewHolder.caption.setText(aPublic.getCaption());
            viewHolder.pid.setText(aPublic.getPost_ID());
            viewHolder.comment.setText(aPublic.getCommCount()+ " Comments");
//            viewHolder.share.setVisibility(View.GONE);
            if(aPublic.getUploadBy().equals(UID))
            {
                viewHolder.friend_req.setVisibility(View.GONE);
            }

//            viewHolder.video_cover.setOnClickListener(view->{
//                mVideoPlayerManager.playNewVideo(null, viewHolder.videoView, aPublic.getImage());
//            });
            if(aPublic.getPost_type().equals("video"))
            {
                Uri videoUri = getMedia(aPublic.getImage());
                viewHolder.videoView.setVideoPath(aPublic.getImage());


               mediaController = new FullScreenMediaController(getContext());
                mediaController.setAnchorView( viewHolder.videoView);

                viewHolder.videoView.setMediaController(mediaController);
                //viewHolder.videoView.start();

                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.carouselView.setVisibility(View.GONE);
                viewHolder.videoView.setVisibility(View.VISIBLE);
                viewHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {



                        mp.start();

                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "Changed");
                                viewHolder.progressBar.setVisibility(View.GONE);
                                viewHolder.videoView.start();
                            }
                        });


                    }
                });
            }


            viewHolder.carouselView.setOnClickListener(view ->  {
                    Intent intent = new Intent(getActivity().getApplicationContext(), EnlargedView.class);
                    intent.putExtra("post_image", aPublic.getImage());
                    startActivity(intent);
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

//        private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
//            @Override
//            public void onPlayerItemChanged(MetaData metaData) {
//
//            }
//        });

     public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            boolean isClicked = false;
            CircleImageView circleImageView;
            TextView name, caption, post_text, pid, all_comments, like_count,uploadtime, comment, isShare;
            ImageView like,  friend_req, overflow, share,chat, sentReq, video_cover;
            ImageView carouselView;
            ProgressBar progressBar;
            VideoView videoView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                circleImageView = itemView.findViewById(R.id.profile_image);
                name = itemView.findViewById(R.id.name);
                like = itemView.findViewById(R.id.image_heart);
                progressBar=itemView.findViewById(R.id.progress);
                friend_req = itemView.findViewById(R.id.image_send_friendreq);
                sentReq = itemView.findViewById(R.id.image_sent_friendReq);
                caption = itemView.findViewById(R.id.caption);
                pid = itemView.findViewById(R.id.PID);
                uploadtime = itemView.findViewById(R.id.time);
                carouselView = itemView.findViewById(R.id.carouselView);
                videoView = itemView.findViewById(R.id.post_video);
                post_text = itemView.findViewById(R.id.post_text);
                chat = itemView.findViewById(R.id.chat);
                overflow = itemView.findViewById(R.id.overflow);
                all_comments = itemView.findViewById(R.id.view_all_comments);
                like_count = itemView.findViewById(R.id.likes_count);
                comment = itemView.findViewById(R.id.comment_count);
                share = itemView.findViewById(R.id.share);
                isShare = itemView.findViewById(R.id.isShare);
//                video_cover = itemView.findViewById(R.id.video_cover_1);

                like.setOnClickListener(this);
                chat.setVisibility(View.GONE);
                overflow.setOnClickListener(this);
                friend_req.setOnClickListener(this);
                circleImageView.setOnClickListener(this);
                all_comments.setOnClickListener(this);
                comment.setOnClickListener(this);
                share.setOnClickListener(this);
//                video_cover.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                if(v == overflow){
                    showPopupMenu(overflow, pid.getText().toString(), getAdapterPosition());
                }
                 if(v == like){
                    if(isClicked==false){
                        LikeDisCall(UID, pid.getText().toString(), "1");
//                       like_count.setText(liked+ " likes");
                        like.setImageResource(R.drawable.ic_heart);
                        isClicked = true;
                    }
                    else {
                        LikeDisCall(UID, pid.getText().toString(), "0");
//                        like_count.setText(liked + "likes");
                        like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        isClicked = false;
                    }
                }
                else if(v == all_comments){
                    Intent i = new Intent(getActivity().getApplicationContext(), PostComment.class);
//                    i.putExtra("comment", "all_comments");
                     i.putExtra("isShare", isShare.getText().toString());
                    i.putExtra("pid", pid.getText().toString());
                    startActivity(i);
                }
                else if(v == friend_req){
                    //Call API here
                    sendFriendRequest(pid.getText().toString(), UID);
                    sentReq.setVisibility(View.VISIBLE);
                    friend_req.setVisibility(View.INVISIBLE);
                 }
                //Comment
                 else if(v == comment){
                     Intent i = new Intent(getActivity().getApplicationContext(), PostComment.class);
//                     i.putExtra("comment", "all_comments");
                     i.putExtra("pid", pid.getText().toString());
                     i.putExtra("isShare", isShare.getText().toString());
                     startActivity(i);
                 }
                else if(v == circleImageView){
                    //navigate to profile activity
                    Intent i  = new Intent(getActivity().getApplicationContext(), EditProfile.class);
                    i.putExtra("postID", pid.getText().toString());
                    startActivity(i);
                }
                else if(v == share){
//                     Toast.makeText(getContext(), ""+getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    if(post_text.getVisibility()==View.VISIBLE){
//                        Log.e(TAG, String.valueOf(url.length));
                        String text = url[getAdapterPosition()];
//                        if(url == null){
//                            Log.e(TAG,"empty array");
//                        }
                        Intent share = new Intent(Intent.ACTION_SEND);
//                        Uri screenshotUri = Uri.parse(post_url);
                        share.setType("text/*");
                        share.putExtra(Intent.EXTRA_TEXT, text);
//                        Intent addIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://images.pexels.com/photos/67636/rose-blue-flower-rose-blooms-67636.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));//whatever you want
//
//                        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
//                        chooser.putExtra(Intent.EXTRA_INTENT, share );
//                        chooser.putExtra(Intent.EXTRA_TITLE, "title");
//
//                        Intent[] intentArray =  {addIntent };
//                        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//                        startActivity(chooser);

                        startActivity(Intent.createChooser(share, "Send To"));//Title of the dialog the system will open
                    }
                    else if(videoView.getVisibility()==View.VISIBLE){
                        String video_url = url[getAdapterPosition()];
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/*");
                        share.putExtra(Intent.EXTRA_TEXT, video_url);

//                        Uri screenshotUri = Uri.parse(video_url);
//                        share.setType("video/*");
//                        share.putExtra(Intent.EXTRA_STREAM, screenshotUri);

                        startActivity(Intent.createChooser(share, "Send To"));//Title of the dialog the system will open
                    }
                    else {
                        String post_url =url[getAdapterPosition()];
                        Intent share = new Intent(Intent.ACTION_SEND);
//                        Uri screenshotUri = Uri.parse(post_url);
//                        share.setType("image/*");
                        share.setType("text/*");
//                        share.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        share.putExtra(Intent.EXTRA_TEXT, post_url);
                        startActivity(Intent.createChooser(share, "Send To"));//Title of the dialog the system will open
                    }
                 }

//                else if(v == video_cover){
//                     mVideoPlayerManager.playNewVideo(null, videoView, url[getAdapterPosition()]);
//                 }

//                videoView.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
//                    @Override
//                    public void onVideoPreparedMainThread() {
//                        // We hide the cover when video is prepared. Playback is about to start
//                        video_cover.setVisibility(View.INVISIBLE);
//                    }
//
//                    @Override
//                    public void onVideoStoppedMainThread() {
//                        // We show the cover when video is stopped
//                        video_cover.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onVideoCompletionMainThread() {
//                        // We show the cover when video is completed
//                        video_cover.setVisibility(View.VISIBLE);
//                    }
//                });
            }
        }
    }

    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            // Media name is an external URL.
            return Uri.parse(mediaName);
        } else {
            // Media name is a raw resource embedded in the app.
            return Uri.parse("android.resource://" + getActivity().getPackageName() +
                    "/raw/" + mediaName);
        }
    }





}
