package com.dynashwet.chatmate.Upload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dynashwet.chatmate.R;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.*;

public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener{

    private static String TAG = "TrimmerActivity";
    private K4LVideoTrimmer k4LVideoTrimmer;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        Intent extraIntent = getIntent();
        String path = "";

        if (extraIntent != null) {
            path = extraIntent.getStringExtra(VideoActivity.EXTRA_VIDEO_PATH);
        }
        //setting progressbar
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.trimming_progress));

        k4LVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (k4LVideoTrimmer != null) {
            k4LVideoTrimmer.setMaxDuration(30);
            k4LVideoTrimmer.setOnTrimVideoListener(this);
            k4LVideoTrimmer.setOnK4LVideoListener(this);
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            k4LVideoTrimmer.setVideoURI(Uri.parse(path));
            k4LVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        progressDialog.show();
    }

    @Override
    public void getResult(Uri uri) {
        progressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(getApplicationContext(), getString(R.string.video_saved_at, uri.getPath()), Toast.LENGTH_SHORT).show();

            }
        });

//        Log.v(TAG, uri.toString());
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.setDataAndType(uri, "video/mp4");
//        startActivity(intent);
//        finish();
        Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
        intent.putExtra("URI", uri.toString());
        startActivity(intent);
        finish();
    }

    @Override
    public void cancelAction() {
        progressDialog.cancel();
        k4LVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(String message) {
        progressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                Toast.makeText(getApplicationContext(), "Video Prepared", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
