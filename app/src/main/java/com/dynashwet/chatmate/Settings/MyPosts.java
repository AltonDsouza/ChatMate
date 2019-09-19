package com.dynashwet.chatmate.Settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.dynashwet.chatmate.Dashboard.CommentsActivity;
import com.dynashwet.chatmate.Dashboard.PublicFragment;
import com.dynashwet.chatmate.Dashboard.ReportUser;
import com.dynashwet.chatmate.Models.PostImages;
import com.dynashwet.chatmate.Models.Public;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.dynashwet.chatmate.Utils.MyGridAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPosts extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private List<Public> paginatedList = new ArrayList<>();

    private List<Public> publics = new ArrayList<>();
    private final static String TAG = "MyPosts";
    private String UID;
    private static int mResults;
    private ListView listView;
    private PostAdapter publicAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        Log.i(TAG,"onCreate: MyPosts created");
        progressDialog = new ProgressDialog(this);
        listView = findViewById(R.id.MyPosts);

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);


        if(getIntent().getStringExtra("UIDFromSearch")!=null){
            UID = getIntent().getStringExtra("UIDFromSearch");
        }
        else {
            UID = pref.getString("UserID", "");
        }
        getPosts();
    }

    private void getPosts(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GetMyPosts,
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
//                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = jsonObject.getJSONArray("Result");
//                    if(publics!=null){
//                        publics.clear();
//                    }
                                if(paginatedList.size()!=0){
                                    paginatedList.clear();
                                }

                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String upload_by="", post_type ="", caption = "", name = "", upload_time = "",
                                            pid = "", pro_pic = "", post_image = "", isLike = "", likes = "", isShare = "", commCount="", isFriend = "";
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
                                        isShare = jsonArray1.getString("IsShare");
                                        isFriend = jsonArray1.getString("IsFriend");

                                        pro_pic = AppConstant.BASE_URL+jsonArray1.getString("Pic");//Profile Image
                                        if(post_type.equals("image")){
                                            post_image = AppConstant.BASE_URL+jsonArray1.getString("PostPath");//Post Image
                                        }
                                        else if(post_type.equals("text")){
                                            post_image =jsonArray1.getString("PostPath");//Post Text
                                        }
                                        else if(post_type.equals("video")){
//                                            post_image = jsonArray1.getString("PostPath");
                                        }
                                        isLike = jsonArray1.getString("LikeState");
                                    }
                                    publics.add(new Public(name, caption, pro_pic, post_image, post_type,
                                            pid, upload_time, isLike, upload_by, likes, isShare, commCount, isFriend));
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

                                publicAdapter = new PostAdapter(paginatedList, getApplicationContext());
                                listView.setAdapter(publicAdapter);
//                    adapter.notifyDataSetChanged();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "No Posts to be displayed!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Connection timeout error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "server couldn't find the authenticated request", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server is not responding", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "Your device is not connected to internet", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Parse Error (because of invalid json or xml)", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID", UID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public class PostAdapter extends BaseAdapter{
        private List<Public> publics;
        private Context context;

        public PostAdapter(List<Public> publics, Context context) {
            this.publics = publics;
            this.context = context;
        }

        @Override
        public int getCount() {
            return publics.size();
        }

        @Override
        public Object getItem(int position) {
            return publics.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_public_listitem, viewGroup, false);
            CircleImageView circleImageView = view.findViewById(R.id.profile_image);
            TextView name = view.findViewById(R.id.name);
            TextView caption = view.findViewById(R.id.caption);
            TextView post_text = view.findViewById(R.id.post_text);
            TextView all_comments = view.findViewById(R.id.view_all_comments);
            TextView like_count = view.findViewById(R.id.likes_count);
            TextView pid = view.findViewById(R.id.PID);

            ImageView overflow = view.findViewById(R.id.overflow);
            ImageView carouselView = view.findViewById(R.id.carouselView);
            TextView comment = view.findViewById(R.id.comment_count);
            ImageView friend_req = view.findViewById(R.id.image_send_friendreq);
            ImageView share = view.findViewById(R.id.share);
            ImageView like = view.findViewById(R.id.image_heart);

            VideoView videoView = view.findViewById(R.id.post_video);

            if(publics.get(position).getPost_type().equals("image")){
                Picasso.with(context).load(publics.get(position).getImage()).into(carouselView);
            }//Image
            else if(publics.get(position).getPost_type().equals("text")){
                carouselView.setVisibility(View.GONE);
                post_text.setVisibility(View.VISIBLE);
                post_text.setText(publics.get(position).getImage());
            }//Text
            else {
                carouselView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                //SetVideo
            }//Video

            Picasso.with(context).load(publics.get(position).getPro_image()).into(circleImageView);
            name.setText(publics.get(position).getName());
            caption.setText(publics.get(position).getCaption());
            like_count.setText(publics.get(position).getLikes());
            pid.setText(publics.get(position).getPost_ID());
            friend_req.setVisibility(View.GONE);
//            comment.setVisibility(View.GONE);
            comment.setText(publics.get(position).getCommCount());

            all_comments.setOnClickListener(view1 -> {
                    //View All Comments
                    Intent i = new Intent(getApplicationContext(), CommentsActivity.class);
                    i.putExtra("comment", "all_comments");
                    i.putExtra("pid", pid.getText().toString());
                    startActivity(i);
            });

            share.setOnClickListener(view1 -> {
                    Toast.makeText(getApplicationContext(), "Work in progress", Toast.LENGTH_SHORT).show();
            });

            overflow.setOnClickListener(view1 -> {
                showPopUp(overflow);
            });

            return view;
        }
    }

    private void showPopUp(View view) {
            // inflate menu
            PopupMenu popup = new PopupMenu(MyPosts.this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_myposts, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
            popup.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_deletepost:
//                    confirmDialogBox();
                    return true;
                default:
            }
            return false;
        }
    }

    private void confirmDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("POST");
        builder.setMessage("Are you sure you want to post ?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            //Delete Post API
        });
        builder.setNegativeButton("No", (dialogInterface, i) ->  {
                dialogInterface.cancel();
        });
        builder.create();
        builder.show();
    }

}
