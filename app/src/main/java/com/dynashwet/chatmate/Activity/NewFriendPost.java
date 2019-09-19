package com.dynashwet.chatmate.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.dynashwet.chatmate.Adapter.MyVideosAdapter;
import com.dynashwet.chatmate.Adapter.VideosAdapterPublic;
import com.dynashwet.chatmate.Dashboard.FriendsFragment;
import com.dynashwet.chatmate.Dashboard.PostText;
import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.Model.MyModel;
import com.dynashwet.chatmate.Models.Public;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class NewFriendPost extends Fragment implements View.OnClickListener {
    @BindView(R.id.rv_home)

    AAH_CustomRecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ArrayList<String> storeContacts = new ArrayList<>();
    Cursor cursor ;
    private boolean isViewShown = false;
    ShimmerLayout shimmer;
    private Mediator mediator;
    private static String url [];
    private static int mResults;
    private List<MyModel> paginatedList = new ArrayList<>();
    MyVideosAdapter adapter;

    private String UID;
    private static final String TAG = "NewFriendPost";
    private String name, phonenumber ;

    private final List<MyModel> modelList = new ArrayList<>();

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    //Default Constructor
    public NewFriendPost(){

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            getContacts();
        } else {
            isViewShown = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"OnCreate:");
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");
        mediator = new Mediator(getContext());

//        getContacts();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.new_public_post, container, false);
        Log.d(TAG,"OnCreatedView:");
        shimmer =(ShimmerLayout) view.findViewById(R.id.shimmer);
        shimmer.startShimmerAnimation();
        recyclerView = view.findViewById(R.id.rv_home);
        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.upload);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                    totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
//                            Toast.makeText(getContext(), "Last Item Wow", Toast.LENGTH_SHORT).show();
                            //Do pagination.. i.e. fetch new data
//                            displayMorePosts();
                        }
                    }
                }
            }
        });

        floatingActionButton.setOnClickListener(this);
        ButterKnife.bind(getActivity());
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == floatingActionButton){
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
    }

    @Override
    public void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)
        recyclerView.stopVideos();
    }

    private void getContacts(){
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        cursor = getActivity().getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        if(storeContacts.size()>0){
            storeContacts.clear();
        }
        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

//            contactsList.add(new Contacts(phonenumber));
            storeContacts.add(phonenumber);
        }
//        Log.e(TAG, storeContacts.toString());
        getFriendsData(storeContacts, UID);
        cursor.close();
    }



    private void getFriendsData(ArrayList<String> data, String UID){
        mediator.getContactData(UID , data, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
//                Log.e("Testpurpose", result);
//                progressDialog.dismiss();
                shimmer.stopShimmerAnimation();
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("Msg");
//                publics.clear();
                if(msg.equals("Success")){
                    Picasso p = Picasso.with(getContext());
                    JSONArray jsonArray = jsonObject.getJSONArray("Result");


                    if(paginatedList.size()!=0 || url!=null){
                        paginatedList.clear();
                        url = null;
                    }
                    url = new String[jsonArray.length()];

                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String upload_by="", post_type ="", caption = "", name = "", upload_time = "",
                                pid = "", pro_pic = "", post_image = "", isLike = "", likes = "", isFriend = "", commCount = "",
                                isShare="", isBlock = "";
                        JSONObject jsonArray1 = jsonObject1.getJSONObject("Info");

                        for(int i1 = 0; i1<jsonArray1.length(); i1++){
//                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i1);
                            upload_by = jsonArray1.getString("UploadBy");
                            post_type = jsonArray1.getString("PostType");
                            caption = jsonArray1.getString("PostCaption");
                            likes = jsonArray1.getString("PostLike");
                            name = jsonArray1.getString("Name");
//                                commCount = jsonArray1.getString("CommCount");
                            isBlock = jsonArray1.getString("IsBlock");
                            isShare = jsonArray1.getString("IsShare");

                            upload_time = jsonArray1.getString("UploadAt");
                            pid = jsonArray1.getString("PID");
                            isFriend = jsonArray1.getString("IsFriend");
                            pro_pic = AppConstant.BASE_URL+jsonArray1.getString("Pic");//Profile Image
                            url[i] = post_image;
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
//                        publics.add(new Public(name, caption, pro_pic, post_image, post_type, pid, upload_time, isLike, upload_by, likes, isFriend, commCount, isShare));
                        //String name, String caption, String profile_image, String image, String post_type, String post_id, String upload_time
                    }
                    int iterations = modelList.size();

                    if(iterations > 10){
                        iterations = 10;
                    }
                    mResults = 10;
                    for(int i = 0; i < iterations; i++){
                        paginatedList.add(modelList.get(i));
                    }

//                                adapter = new PublicAdapter(paginatedList);
//                                recyclerView.setAdapter(adapter);
//                               adapter.notifyDataSetChanged();

                    adapter = new MyVideosAdapter(paginatedList, p,getActivity());
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

                    try{
                        recyclerView.preDownload(urls);

                    }catch (Exception ex){
//                                    Toast.makeText((), ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    recyclerView.setAdapter(adapter);
                    //call this functions when u want to start autoplay on loading async lists (eg firebase)
                    recyclerView.smoothScrollBy(0,1);
                    recyclerView.smoothScrollBy(0,-1);
                }
                else if(msg.equals("Error")){
                    //No posts yet , Add or invite some friends in order to see their posts
//                    noposts.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }




}
