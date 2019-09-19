package com.dynashwet.chatmate.Dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.Models.Comments;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.dynashwet.chatmate.Utils.CommentListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private ImageView imageView, backArrow;
    private ListView listView;
    private TextView noComments;
    private String[] comment_username;
    private String[] comment;
    private String[] comment_images;
    private List<Comments>  comments = new ArrayList<>();
    private JSONArray jsonArray;
    private JSONObject jsonArray1;
    private JSONArray jsonArray2;
    private String Pid, UID;
    private ProgressDialog progressDialog;
    private CommentListAdapter commentListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_comment);
        editText = findViewById(R.id.comment);
        imageView = findViewById(R.id.ivPostComment);
        listView = findViewById(R.id.listView);
        noComments = findViewById(R.id.noComments);
        backArrow = findViewById(R.id.backArrow);
        progressDialog = new ProgressDialog(this);

        if(getIntent().getStringExtra("comment").equals("all_comments")){
            editText.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }

        Pid = getIntent().getStringExtra("pid");
        Toast.makeText(getApplicationContext(),Pid,Toast.LENGTH_LONG).show();

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");

        commentListAdapter = new CommentListAdapter(this, comments);
        listView.setAdapter(commentListAdapter);


        imageView.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        loadComments(Pid);
    }
    private void loadComments(final String PostID){
        progressDialog.setTitle("COMMENTS");
        progressDialog.setMessage("Displaying Comments...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                jsonArray = jsonObject.getJSONArray("Result");


                                    String name = "", pid = "", pro_pic = "", text = "";
//                                        jsonArray1 = jsonObject1.getJSONObject("Info");
                                        for(int i = 0; i<jsonArray.length(); i++){
                                         JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                                name = jsonObject2.getString("Name");
                                                pro_pic = AppConstant.BASE_URL+jsonObject2.getString("ProfilePic");//Profile Image
                                                text = jsonObject2.getString("CommentText");
                                                comments.add(new Comments(text, name, pro_pic));
                                        }
//                                    else {
//                                        //No comments
//
//                                    }
                            }
                            else if(msg.equals("Error")){
                                String reason = jsonObject.getString("Reason");
                                if(reason.equals("No Comments")){
                                    noComments.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                }
                            }
                            commentListAdapter.notifyDataSetChanged();

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
                params.put("PID", PostID);
//                params.put("UID", UID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void insertComment(final String UID, final String comment, final String postID){
        progressDialog.setMessage("Adding commment..");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.INSERT_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){

                                Intent i = new Intent(getApplicationContext(), TabActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                                Toast.makeText(getApplicationContext(), "Added Comment!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Error adding comment.", Toast.LENGTH_SHORT).show();
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
                params.put("PID", postID);
                params.put("CommentText", comment);
                params.put("CommentBy", UID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if(v == imageView)
            insertComment(UID, editText.getText().toString(), Pid);
        else if(v == backArrow)
            finish();
    }
}
