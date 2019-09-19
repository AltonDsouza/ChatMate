package com.dynashwet.chatmate.Upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.Permissions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";

    private static final int PHOTO_FRAGMENT = 1;
    private static final int GALLERY_FRAGMENT = 2;
    private static final int CAMERA_REQUEST_CODE = 5;
    ArrayList<Uri> image_uris = new ArrayList<Uri>();

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_photo);
        Log.d(TAG, "onCreate: started");
        Button launchCamera = findViewById(R.id.camera);
        imageView = findViewById(R.id.cameraImage);
        Log.d(TAG,"onClick: starting camera");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        launchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Log.d(TAG,"onClick: starting camera");
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

        ImageView shareClose = (ImageView) findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: Closing the gallery fragment.");
                finish();
            }
        });

        TextView nextScreen = (TextView)findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: navigating to the final share screen");
                Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                intent.putExtra("selected_image", image_uris);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            image_uris.add(getImageUri(getApplicationContext(), photo));
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
