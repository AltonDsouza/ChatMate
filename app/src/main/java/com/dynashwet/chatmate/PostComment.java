package com.dynashwet.chatmate;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.Adapter.CommentAdapter;
import com.dynashwet.chatmate.Models.Comments;
import com.dynashwet.chatmate.Models.NewComment;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;

public class PostComment extends AppCompatActivity {

    private List<NewComment> lstAnime;
    RecyclerView recyclerView;
    ImageView send;
    EditText message;
    String UID, isShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        lstAnime = new ArrayList<>() ;
        recyclerView=(RecyclerView)  findViewById(R.id.recyclerViewid);
        send = (ImageView) findViewById(R.id.send);
        message =(EditText) findViewById(R.id.message);
        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");

        if(getIntent().getStringExtra("isShare")!=null){
            isShare = getIntent().getStringExtra("isShare");
            if(isShare.equals("No")){
                send.setEnabled(false);
                message.setText("Due to privacy of user you can't comment");
                message.setEnabled(false);
            }
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    sendReply();
            }
        });
        getReply();
    }


    private void sendReply() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.INSERT_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("Msg").equals("Success"))
                            {
                                getReply();
                                message.setText("");
                                //String lk = comment.getText().toString().substring(0,1);
                                //comment.setText(String.valueOf(Integer.parseInt(lk)+1)+" comments");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        // progressDialog.dismiss();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("PID", getIntent().getStringExtra("pid"));
                params.put("CommentText", message.getText().toString().trim());
                params.put("CommentBy", UID);
                return params;

            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }



    public  void  getReply()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                          //  Toast.makeText(getApplicationContext(),obj.getString("Msg"),Toast.LENGTH_LONG).show();
                                JSONArray jsonDevices = obj.getJSONArray("Result");
                                lstAnime.clear();
                                for (int i = 0; i < jsonDevices.length(); i++)
                                {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    NewComment anime = new NewComment();
                                    anime.setImage(AppConstant.BASE_URL+d.getString("ProfilePic"));
                                   // anime.setImage(R.drawable.defaultcomment);
                                    anime.setReply(d.getString("CommentText"));
                                    anime.setName(d.getString("Name"));
                                    anime.setCommentID(d.getString("CID"));
                                    anime.setPID(d.getString("PID"));
                                    anime.setUser_id(d.getString("UID"));
                                    anime.setCommentBy(d.getString("CommentBy"));
                                    anime.setUploadBy(d.getString("UploadBy"));
                                    lstAnime.add(anime);
                                }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CommentAdapter myadapter = new CommentAdapter(getApplicationContext(),lstAnime) ;
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.scrollToPosition(myadapter.getItemCount()-1);
                        recyclerView.setAdapter(myadapter);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("PID", getIntent().getStringExtra("pid"));
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
