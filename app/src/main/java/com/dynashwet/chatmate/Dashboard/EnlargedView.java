package com.dynashwet.chatmate.Dashboard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dynashwet.chatmate.R;
import com.squareup.picasso.Picasso;

public class EnlargedView extends AppCompatActivity {
private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enlarged_view);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        imageView = findViewById(R.id.post_image);

        Picasso.with(this).load(getIntent().getStringExtra("post_image")).into(imageView);
//        this.setFinishOnTouchOutside(false);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
