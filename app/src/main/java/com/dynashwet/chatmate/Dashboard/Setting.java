package com.dynashwet.chatmate.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dynashwet.chatmate.NavigationDashboard.BlockedList;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Settings.MyPosts;

public class Setting extends AppCompatActivity implements View.OnClickListener {

    private TextView posts, changePwd, blockedList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        fVBID();
    }

    private void fVBID() {
        posts = findViewById(R.id.posts);
        changePwd = findViewById(R.id.changePwd);
        blockedList = findViewById(R.id.blockedUsers);

        posts.setOnClickListener(this);
        changePwd.setOnClickListener(this);
        blockedList.setOnClickListener(this);

        changePwd.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view == posts){
            //Display posts of user till date in gridview
            startActivity(new Intent(getApplicationContext(), MyPosts.class));
        }
        else if(view == blockedList){
            //Show blocked user List
            startActivity(new Intent(getApplicationContext(), BlockedList.class));
        }
        else if(view == changePwd){
            //Change password
        }
    }
}
