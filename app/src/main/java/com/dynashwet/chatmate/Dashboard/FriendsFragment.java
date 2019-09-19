package com.dynashwet.chatmate.Dashboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dynashwet.chatmate.Chat;
import com.dynashwet.chatmate.FullScreenMediaController;
import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.Models.Contacts;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment implements View.OnClickListener {
    private static  final String TAG = "FriendsFragment";
    private List<Public> publics = new ArrayList<>();
    private boolean _hasLoadedOnce= false; // your boolean field
    private List<Contacts> contactsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private List<Public> paginatedList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
//    private EditText editText;
private MediaController mediaController;


    private static ProgressDialog progressDialog;
    private static int mResults;
    private FriendsAdapter friendsAdapter;
    private TextView noposts;
    String name, phonenumber, UID ;
    Cursor cursor ;
    ArrayList<String> storeContacts = new ArrayList<>();
    private Mediator mediator;

    public FriendsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // loadDummyData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public, container, false);
        recyclerView = (RecyclerView) view
                .findViewById(R.id.publicList);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.upload);
//        editText = (EditText)view.findViewById(R.id.postown);
//        editText.setOnClickListener(this);
        noposts = (TextView)view.findViewById(R.id.noposts);
        progressDialog = new ProgressDialog(getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        floatingActionButton.setOnClickListener(this);

        noposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display contacts list not registered

            }
        });



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContacts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

//        FriendsAdapter adapter = new FriendsAdapter(publics);

        // showAlertDialog();
        //   recyclerView.setAdapter(adapter);
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");
        mediator = new Mediator(getActivity());
        getContacts();
        setHasOptionsMenu(true);
        return view;
    }

    //    public void loadDummyData(){
//        publics = new ArrayList<Public>();
//        publics.add(new Public("Friend1", R.drawable.ic_person_black_24dp, new int[]{R.drawable.index,
//                R.drawable.auto_campus_transparent, R.drawable.flower}));
//
//        publics.add(new Public("Friend2",R.drawable.ic_person_black_24dp, new int[]{}));
//
//    }




    private void showAlertDialog(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle("Sync Contacts");
        builder.setMessage("Needs to sync contacts in order to display posts!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Get Contacts
                getContacts();
            }
        });
        builder.create();
        builder.show();
    }



    private void getContacts(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
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
                Log.e("Testpurpose", result);
                progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("Msg");
                    publics.clear();
                    if(msg.equals("Success")){
                        JSONArray jsonArray = jsonObject.getJSONArray("Result");

                        if(paginatedList.size()!=0){
                            paginatedList.clear();
                        }

                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String upload_by="", post_type ="", caption = "", name = "", upload_time = "",
                                    pid = "", pro_pic = "", post_image = "", isLike = "", likes = "", isFriend = "", commCount = "", isShare="";
                            JSONObject jsonArray1 = jsonObject1.getJSONObject("Info");

                            for(int i1 = 0; i1<jsonArray1.length(); i1++){
//                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i1);
                                upload_by = jsonArray1.getString("UploadBy");
                                post_type = jsonArray1.getString("PostType");
                                caption = jsonArray1.getString("PostCaption");
                                likes = jsonArray1.getString("PostLike");
                                name = jsonArray1.getString("Name");
//                                commCount = jsonArray1.getString("CommCount");
                                isShare = jsonArray1.getString("IsShare");
                                upload_time = jsonArray1.getString("UploadAt");
                                pid = jsonArray1.getString("PID");
                                isFriend = jsonArray1.getString("IsFriend");
                                pro_pic = AppConstant.BASE_URL+jsonArray1.getString("Pic");//Profile Image
                                if(post_type.equals("image")){
                                    post_image = AppConstant.BASE_URL+jsonArray1.getString("PostPath");//Post Image
                                }
                                else if(post_type.equals("text")){
                                    post_image =jsonArray1.getString("PostPath");//Post Text
                                }
                                isLike = jsonArray1.getString("LikeState");
                            }
                            publics.add(new Public(name, caption, pro_pic, post_image, post_type, pid, upload_time, isLike, upload_by, likes, isFriend, commCount, isShare));
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

                        friendsAdapter = new FriendsAdapter(paginatedList);

                        recyclerView.setAdapter(friendsAdapter);
                    }
                    else if(msg.equals("Error")){
                            //No posts yet , Add or invite some friends in order to see their posts
                            noposts.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                    }
            }
        });
    }


    private void LikeDisCall(String UID, String Post_ID, String state){
        mediator.LikeDisCon(UID, Post_ID, state, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("Msg");
                if (msg.equals("SUCCESS")){
//                    Toast.makeText(getActivity(), "Liked", Toast.LENGTH_SHORT).show();
                }
                else {

                }
            }
        });
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
                friendsAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        if(view == floatingActionButton){
//            startActivity(new Intent(getActivity(), Share.class));
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

    public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{

        private List<Public> list;
        RequestOptions option;
        public FriendsAdapter(List<Public> list) {
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
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            final Public aPublic = list.get(i);
            if(aPublic.getPost_type().equals("image")){

                viewHolder.carouselView.setVisibility(View.VISIBLE);
                viewHolder.post_text.setVisibility(View.GONE);
                Picasso.with(getActivity()).load(aPublic.getImage()).into(viewHolder.carouselView);
                //Glide.with(getContext()).load("http://taxembassy.com/ChatMate/Dyna-Upload/14/5d554356ae14a.jpeg").into(viewHolder.carouselView);
            }
            else if(aPublic.getPost_type().equals("text")){
                viewHolder.carouselView.setVisibility(View.GONE);
                viewHolder.post_text.setVisibility(View.VISIBLE);
                viewHolder.post_text.setText(aPublic.getImage());
            }
            //Condition for video here
            else if(aPublic.getPost_type().equals("video"))
            {
                //do something
                viewHolder.carouselView.setVisibility(View.GONE);
                viewHolder.post_text.setVisibility(View.GONE);
                viewHolder.videoView.setVisibility(View.VISIBLE);

            }

            if(aPublic.getIsLike().equals("1")){
                viewHolder.like.setImageResource(R.drawable.ic_heart);
            }
            else if(aPublic.getIsLike().equals("0")){
                viewHolder.like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }


            if(aPublic.getIsFriend().equals("Yes")){
                viewHolder.share.setVisibility(View.VISIBLE);
            }
            else if(aPublic.getIsFriend().equals("No")){
                viewHolder.share.setVisibility(View.GONE);
            }
            else
            {
                //Req

            }


            if(aPublic.getPost_type().equals("video"))
            {
                Toast.makeText(getContext(),aPublic.getImage(),Toast.LENGTH_LONG).show();
                Uri videoUri = getMedia("http://topautocareindia.com/ChatMate/Upload/TestVideo/1566050304.mp4");
                viewHolder.videoView.setVideoURI(videoUri);


                mediaController = new FullScreenMediaController(getContext());
                mediaController.setAnchorView( viewHolder.videoView);

                 viewHolder.videoView.setMediaController(mediaController);
                //viewHolder.videoView.start();

                //viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.carouselView.setVisibility(View.GONE);
                viewHolder.videoView.setVisibility(View.VISIBLE);
                viewHolder.post_text.setVisibility(View.GONE);
                viewHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {



                        mp.start();

                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "Changed");
                                //viewHolder.progressBar.setVisibility(View.GONE);
                                viewHolder.videoView.start();
                            }
                        });
                    }
                });
            }


            viewHolder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i  = new Intent(getActivity().getApplicationContext(), Chat.class);
                    i.putExtra("friendid", aPublic.getUploadBy());
                    i.putExtra("dp", aPublic.getPro_image());
                    i.putExtra("name", aPublic.getName());
                    startActivity(i);
                }
            });

            viewHolder.likes.setText(aPublic.getLikes()+" Likes");
            //Picasso.with(getActivity()).load(aPublic.getPro_image()).into(viewHolder.circleImageView);
            Glide.with(getContext()).load(aPublic.getPro_image()).apply(option).into(viewHolder.circleImageView);
            viewHolder.name.setText(aPublic.getName());
            viewHolder.uploadtime.setText(aPublic.getUpload_time());
            viewHolder.comment.setText(aPublic.getCommCount());
            viewHolder.caption.setText(aPublic.getCaption());
            viewHolder.pid.setText(aPublic.getPost_ID());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CircleImageView circleImageView;
            boolean isClicked = false;

            TextView name, caption, post_text, pid, likes,comment,uploadtime;
            ImageView like,  friend_req, share,chat, video_cover;
            ImageView carouselView;
            VideoView videoView;
            //No friend request in friends

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                circleImageView = itemView.findViewById(R.id.profile_image);
                name = itemView.findViewById(R.id.name);
                like = itemView.findViewById(R.id.image_heart);
                comment = itemView.findViewById(R.id.comment_count);
                share = itemView.findViewById(R.id.share);
                uploadtime = itemView.findViewById(R.id.time);
                friend_req = itemView.findViewById(R.id.image_send_friendreq);
                pid = itemView.findViewById(R.id.PID);
                likes = itemView.findViewById(R.id.likes_count);
                chat = itemView.findViewById(R.id.chat);

                caption = itemView.findViewById(R.id.caption);
                carouselView = itemView.findViewById(R.id.carouselView);
                videoView = itemView.findViewById(R.id.post_video);
                post_text = itemView.findViewById(R.id.post_text);
//                video_cover = itemView.findViewById(R.id.video_cover_1);

                comment.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
                friend_req.setVisibility(View.GONE);


                like.setOnClickListener(this);
                chat.setOnClickListener(this);
//                overflow.setOnClickListener(this);
                friend_req.setOnClickListener(this);
                comment.setOnClickListener(this);
                circleImageView.setOnClickListener(this);
//                video_cover.setOnClickListener(this);
//                all_comments.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(v == like){
                    if(isClicked==false){
                        LikeDisCall(UID, pid.getText().toString(), "1");
                        like.setImageResource(R.drawable.ic_heart);
                        isClicked = true;
                    }
                    else {
                        LikeDisCall(UID, pid.getText().toString(), "0");
                        like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        isClicked = false;
                    }
                }

                //Comment
                else if(v == comment){
                    Intent i = new Intent(getActivity().getApplicationContext(),CommentsActivity.class);
                    i.putExtra("pid", pid.getText().toString());
                    startActivity(i);
                }

                else if(v == circleImageView){
                    //navigate to profile activity
                    Intent i  = new Intent(getActivity().getApplicationContext(), EditProfile.class);
                    i.putExtra("postID", pid.getText().toString());
                    startActivity(i);
                }
                else if(v == friend_req)
                {
                    PublicFragment fragment = new PublicFragment();
                    fragment.sendFriendRequest(pid.getText().toString(), UID);
                }


            }
        }
    }
    private Uri getMedia(String mediaName)
    {
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
