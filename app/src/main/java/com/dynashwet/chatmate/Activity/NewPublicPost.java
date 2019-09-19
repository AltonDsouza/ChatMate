package com.dynashwet.chatmate.Activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.danikula.videocache.HttpProxyCacheServer;
import com.dynashwet.chatmate.Adapter.MyVideosAdapter;
import com.dynashwet.chatmate.Adapter.VideosAdapterPublic;
import com.dynashwet.chatmate.Dashboard.PostText;
import com.dynashwet.chatmate.Model.MyModel;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.RequestHandler;
import com.dynashwet.chatmate.Upload.GalleryActivity;
import com.dynashwet.chatmate.Upload.PhotoActivity;
import com.dynashwet.chatmate.Upload.VideoActivity;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class NewPublicPost extends Fragment implements  View.OnClickListener {

    private static final String TAG = "PublicFragment";
    ShimmerLayout shimmer;
    File root;
    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    private boolean isViewShown = false;
    private ProgressBar progressBar;

    private String UID;
    private static int mResults = 0;
    private final List<MyModel> modelList = new ArrayList<>();
    private final List<MyModel> paginatedList = new ArrayList<>();

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    VideosAdapterPublic mAdapter;


    public static class MyApplication extends Application {
        private static HttpProxyCacheServer proxy;

        public static HttpProxyCacheServer getProxy() {
            return proxy;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            proxy = new HttpProxyCacheServer(this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
//            loadData();
            loadData();
        } else {
            isViewShown = false;
        }
    }


    void showProgressView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressView() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_public_post, container, false);
        recyclerView = view.findViewById(R.id.rv_home);
        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.upload);
         root = new File(Environment.getDataDirectory()
                + "/tempstatus/");
//        progressBar = view.findViewById(R.id.paginatedLoader);
        shimmer =(ShimmerLayout) view.findViewById(R.id.shimmer);
//        recyclerView.setVisibility(View.GONE);
        shimmer.startShimmerAnimation();
        floatingActionButton.setOnClickListener(this);
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");
        ButterKnife.bind(getActivity());

        if (!isViewShown) {
            loadData();
        }

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if(dy > 0) //check for scroll down
//                {
//                    visibleItemCount = recyclerView.getLayoutManager().getChildCount();
//                    totalItemCount = recyclerView.getLayoutManager().getItemCount();
//                    pastVisiblesItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//
//                    if (loading)
//                    {
//                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
//                        {
//                            loading = false;
////                            Toast.makeText(getContext(), "Last Item Wow", Toast.LENGTH_SHORT).show();
//                            //Do pagination.. i.e. fetch new data
//                            displayMorePosts();
////                            hideProgressView();
//                        }
//                    }
//                }
//            }
//        });

        //loadData();
        return view;
    }


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



    public void loadData(){
//        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://topautocareindia.com/ChatMate/PostMaster/GetPublicPost",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            shimmer.stopShimmerAnimation();
//                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                Picasso p = Picasso.with(getContext());
                                Log.e("new", "onLoadAPI: SUCCESS");

//                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = jsonObject.getJSONArray("Result");
                                if(modelList!=null){
                                    modelList.clear();
                                }

//                                url = new String[jsonArray.length()];//Faced Error when placed inside for loop
////                                UploadBys = new String[jsonArray.length()];

                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String upload_by="", post_type ="", caption = "", name = "", upload_time = "",
                                            pid = "", pro_pic = "", post_image = "", isLike = "", likes = "", isShare = "", commCount = "",
                                            isFriend="", isBlock="";

                                    JSONObject jsonArray1 = jsonObject1.getJSONObject("Info");
                                    for(int i1 = 0; i1<jsonArray1.length(); i1++){
//                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i1);
                                        // Toast.makeText(getContext(),UID,Toast.LENGTH_LONG).show();
                                        upload_by = jsonArray1.getString("UploadBy");
                                        post_type = jsonArray1.getString("PostType");
//                                        if(!post_type.equals("video"))

//                                        Toast.makeText(getContext(),jsonArray1.getString("PostType"), Toast.LENGTH_SHORT).show();
                                        isBlock = jsonArray1.getString("IsBlock");
                                        likes = jsonArray1.getString("PostLike");
                                        caption = jsonArray1.getString("PostCaption");
                                        name = jsonArray1.getString("Name");
                                        upload_time = jsonArray1.getString("UploadAt");
                                        pid = jsonArray1.getString("PID");
                                        commCount = jsonArray1.getString("CommCount");
                                        isFriend = jsonArray1.getString("IsFriend");
                                        isShare = jsonArray1.getString("IsShare");
                                        pro_pic = "http://topautocareindia.com/ChatMate/"+jsonArray1.getString("Pic");//Profile Image
//                                        url[i] = post_image;
                                        isLike = jsonArray1.getString("LikeState");
//                                        }

//                                        UploadBys[i1] = jsonArray1.getString("UploadBy");

                                    }

                                    if(post_type.equals("image"))
                                    {

                                        post_image = "http://topautocareindia.com/ChatMate/"+jsonArray1.getString("PostPath");//Post Image
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
                                    if(post_type.equals("text")){
                                        post_image = jsonArray1.getString("PostPath");//Post Image
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
                                    if(post_type.equals("video"))
                                    {
                                        post_image = "http://topautocareindia.com/ChatMate/"+jsonArray1.getString("PostPath");//Post Image
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
//                                    publics.add(new Public(name, caption, pro_pic, post_image, post_type,
//                                            pid, upload_time, isLike, upload_by, likes, isFriend, commCount, isShare));

                                    //String name, String caption, String profile_image, String image, String post_type, String post_id, String upload_time
                                }

                                VideosAdapterPublic mAdapter = new VideosAdapterPublic(modelList, p,getActivity());
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());

                                //todo before setAdapter
                                recyclerView.setActivity(getActivity());

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
                                try {
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
                                Toast.makeText(getContext(), "No Posts to be displayed!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), "Connection timeout", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getContext(), "server couldn't find the authenticated request", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getContext(), "Server is not responding", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getContext(), "Your device is not connected to internet", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getContext(), "Parse Error (because of invalid json or xml)", Toast.LENGTH_SHORT).show();
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

        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
    @Override
    public void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)
        recyclerView.stopVideos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.stopVideos();
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerView.stopVideos();
    }



    public void displayMorePosts(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");
//        showProgressView();

        try{
            if(modelList.size() > mResults && modelList.size() > 0){

                int iterations;
                if(modelList.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = modelList.size() - mResults;
                }

                //add the new photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    paginatedList.add(modelList.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }


    public  void  loader()
    {

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, 10000);
    }
//

}
