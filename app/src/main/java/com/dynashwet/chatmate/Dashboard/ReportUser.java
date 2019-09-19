package com.dynashwet.chatmate.Dashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.VolleyCallBack;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportUser extends AppCompatActivity implements View.OnClickListener {

    private EditText post_edit;
    private Button submit;
    private Mediator mediator;
    private ProgressDialog progressDialog;
    private String UID, PostID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_post_text);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        fVBID();

        SharedPreferences pref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");
        PostID = getIntent().getStringExtra("POSTID");
        submit.setOnClickListener(this);
    }

    private void fVBID() {
        post_edit = findViewById(R.id.post_edit);
        submit = findViewById(R.id.submit_text);
        mediator = new Mediator(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        if(post_edit.getText().toString().length()==0){
            post_edit.setError("Type something to report!");
            post_edit.requestFocus();
        }
        else {
            //post report
            report_post(PostID, post_edit.getText().toString(), UID);
        }
    }

    private void report_post(String postID, String message, String UID){
        mediator.reportPost(postID, message, UID, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("Msg");
                if(msg.equals("Success")){
                    Toast.makeText(getApplicationContext(), "Reported Post to Admin", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
