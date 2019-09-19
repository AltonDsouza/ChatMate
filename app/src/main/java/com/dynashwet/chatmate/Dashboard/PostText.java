package com.dynashwet.chatmate.Dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Upload.NextActivity;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostText extends AppCompatActivity implements View.OnClickListener {

    private EditText post_edit;
    private Button submit;
    private ProgressDialog progressDialog;
    private ArrayList<String> text = new ArrayList<>();
    private String UID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_post_text);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        post_edit = findViewById(R.id.post_edit);
        submit = findViewById(R.id.submit_text);
        progressDialog = new ProgressDialog(this);

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
            //Validation
            if(post_edit.getText().toString().length()==0){
                post_edit.setError("Type something to post!");
                post_edit.requestFocus();
            }
            else {
                if(text.size()>0){text.clear();}
//                text.add(post_edit.getText().toString());
//                upload_text(UID, text);
                Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                intent.putExtra("text", post_edit.getText().toString());
                startActivity(intent);
            }
    }
//
//    private void upload_text(final String UID, final ArrayList<String> text){
//        progressDialog.setMessage("Posting....");
//        progressDialog.show();
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.UPLOAD,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        progressDialog.dismiss();
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String msg = jsonObject.getString("Msg");
//                            if(msg.equals("Success")){
//                                Toast.makeText(getApplicationContext(), "Post updated!", Toast.LENGTH_SHORT).show();
//
//                                Intent i = new Intent(getApplicationContext(), TabActivity.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(i);
//                            }
//                            else if(msg.equals("Error")){
//                                String reason = jsonObject.getString("Reason");
//                                if(reason.equals("NoMedia")){
//                                    Toast.makeText(getApplicationContext(), "No media", Toast.LENGTH_SHORT).show();
//                                }
//                                else if(reason.equals("NoUser")){
//                                    Toast.makeText(getApplicationContext(), "No user", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//
//
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(getApplicationContext(), "Connection timeout error", Toast.LENGTH_SHORT).show();
//
//                } else if (error instanceof AuthFailureError) {
//                    Toast.makeText(getApplicationContext(), "server couldn't find the authenticated request", Toast.LENGTH_SHORT).show();
//                } else if (error instanceof ServerError) {
//                    Toast.makeText(getApplicationContext(), "Server is not responding", Toast.LENGTH_SHORT).show();
//                } else if (error instanceof NetworkError) {
//                    Toast.makeText(getApplicationContext(), "Your device is not connected to internet", Toast.LENGTH_SHORT).show();
//                } else if (error instanceof ParseError) {
//                    Toast.makeText(getApplicationContext(), "Parse Error (because of invalid json or xml)", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Dyna-UserID", UID);
//
//                for (int i = 0; i < text.size(); i++)
//                {
//                    params.put("Dyna-UserPosts["+i+"]", text.get(i));
//                }
////                params.put("Dyna-Caption", caption);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }

}
