package com.dynashwet.chatmate.Upload;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.dynashwet.chatmate.Dashboard.TabActivity;
import com.dynashwet.chatmate.Models.Public;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.Permissions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    private static  int GALLERY_REQ = 12;

    ArrayList<Uri> image_uris = new ArrayList<Uri>();
//    private String path;
    private AppCompatImageView imageView;
    private Button pick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery);
        Log.d(TAG, "onCreate: started");

        imageView = (AppCompatImageView)findViewById(R.id.image);
        pick = (Button)findViewById(R.id.pick);

        Log.d(TAG,"onClick: Going to the gallery");
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQ);

        ImageView shareClose = (ImageView)findViewById(R.id.ivCloseShare);
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

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: Going to the gallery");
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQ);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            CropImage.activity(selectedImage)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri uri = result.getUri();
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            imageView.setImageURI(uri);
            image_uris.add(uri);
        }
        }
}


