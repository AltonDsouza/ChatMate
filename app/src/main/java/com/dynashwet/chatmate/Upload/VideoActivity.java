package com.dynashwet.chatmate.Upload;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.dynashwet.chatmate.Models.ResultObject;
import com.dynashwet.chatmate.R;
import life.knowledge4.videotrimmer.utils.FileUtils;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    private static final String TAG = "VideoActivity";
    private static final int GALLERY = 1;
    private Button button;
    private static String selectedVideoPath;
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video);
        button = findViewById(R.id.video);
        videoView = findViewById(R.id.videoView);

        button.setOnClickListener(this);
        ImageView shareClose = findViewById(R.id.ivCloseShare);
        if(getIntent().getStringExtra("URI")!=null){
            selectedVideoPath = getIntent().getStringExtra("URI");
            Uri myUri = Uri.parse(getIntent().getStringExtra("URI"));

            Log.v(TAG, selectedVideoPath);
            videoView.setVideoURI(myUri);
            videoView.requestFocus();
            videoView.start();
        }
//        videoView.setVideoPath("http://taxembassy.com/ChatMate/Upload/TestVideo/1564657034.mp4");
//        videoView.start();

        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: Closing the gallery fragment.");
                finish();
            }
        });

        TextView nextScreen = findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: navigating to the final share screen");
                Intent intent = new Intent(getApplicationContext(), NextActivity.class);
//                intent.putExtra("", );
                Bundle bundle = new Bundle();
                bundle.putString("selected_video", selectedVideoPath);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void chooseVideoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onClick(View v) {
        chooseVideoFromGallery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY) {
            if (data != null) {
                final Uri contentURI = data.getData();
                if (contentURI != null) {
                    startTrimActivity(contentURI);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                }
//               selectedVideoPath = getPath(contentURI);
//                Log.d("path",selectedVideoPath);
//                videoView.setVideoURI(contentURI);
//                videoView.requestFocus();
//                videoView.start();
            }
        }
    }

    public void startTrimActivity(Uri uri){
        Intent intent = new Intent(this, TrimmerActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri));
        startActivity(intent);
    }

//    public String getPath(Uri uri) {
//        Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = getApplicationContext().getContentResolver().query(
//                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
//        cursor.close();
//        return path;
//    }
}
