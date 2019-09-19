package com.dynashwet.chatmate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private ImageView imageView, sendFriendReq, sentFriendReq, overflow, heart, share, chat;
    private TextView likeCounts, commCounts, name, timeline, comment, PID, caption;
    private CircleImageView circleImageView;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        fVBID();

    }



    private void getPostData(String PostID){
       progressBar.setVisibility(View.VISIBLE);
            StringRequest request = new StringRequest(Request.Method.POST, AppConstant.DeletePost,
                    success-> {
                progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject object = new JSONObject(success);
                            String msg = object.getString("Msg");
                            if(msg.equals("Success")){
                                JSONArray jsonArray = object.getJSONArray("result");
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

                                        isLike = jsonArray1.getString("LikeState");
                                    }

                                    if(post_type.equals("image")){
                                        post_image = AppConstant.BASE_URL+jsonArray1.getString("PostPath");//Post Image
                                    }
                                    else if(post_type.equals("text")){
                                        post_image = jsonArray1.getString("PostPath");//Post Text
                                    }
                                    else if(post_type.equals("video")){
                                        post_image = AppConstant.BASE_URL+jsonArray1.getString("PostPath");//Post Video
                                    }
//                        publics.add(new Public(name, caption, pro_pic, post_image, post_type, pid, upload_time, isLike, upload_by, likes, isFriend, commCount, isShare));
                                    //String name, String caption, String profile_image, String image, String post_type, String post_id, String upload_time
                                }

                            }
                            else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }, error -> {
                progressBar.setVisibility(View.GONE);

            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
//                    params.put("Dyna-UserID",uid);
                    params.put("Dyna-PostID",PostID);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);


    }
















    private void fVBID() {
        imageView = findViewById(R.id.carouselView);
        sendFriendReq = findViewById(R.id.image_send_friendreq);
        sentFriendReq = findViewById(R.id.image_sent_friendReq);
        overflow = findViewById(R.id.overflow);
        heart = findViewById(R.id.image_heart);
        share = findViewById(R.id.share);
        chat = findViewById(R.id.chat);
        PID = findViewById(R.id.PID);
        likeCounts = findViewById(R.id.likes_count);
        commCounts = findViewById(R.id.comment_count);
        caption = findViewById(R.id.caption);
        comment = findViewById(R.id.view_all_comments);
        progressBar = findViewById(R.id.progress);
    }

}
