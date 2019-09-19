package com.dynashwet.chatmate.Upload;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.Dashboard.TabActivity;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.RequestHandler;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.dynashwet.chatmate.Utils.Upload;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NextActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "NextActivity";
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> images_to_pass = new ArrayList<>();
    private ArrayList<String> text_to_pass = new ArrayList<>();
    private static ProgressDialog progressDialog;
    private ImageView imageView;
    private String path, UID;
    private TextView textView;
    private Spinner spinner;
//    private String [] video;
    List<String> spinnerArray =  new ArrayList<String>();
    private String [] privacyID;
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        textView = (TextView)findViewById(R.id.description);
        progressDialog = new ProgressDialog(this);
        imageView = (ImageView)findViewById(R.id.imageShare);
        spinner = findViewById(R.id.privacySpinner);
        videoView = findViewById(R.id.videoShare);
        spinner.setOnItemSelectedListener(this);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");

      //  Log.e(TAG, imageslist.toString());
        if(getIntent().getStringExtra("image")!=null){
            Picasso.with(this).load(getIntent().getStringExtra("image")).into(imageView);
        }
        else if(getIntent().getStringExtra("text")!=null){
            imageView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView.setText(getIntent().getStringExtra("text"));
            text_to_pass.add(textView.getText().toString());
        }
        else {
            ArrayList<Uri> imageslist = (ArrayList<Uri>) getIntent().getSerializableExtra("selected_image");
            if(imageslist==null){
                imageView.setVisibility(View.GONE);
               Bundle b = getIntent().getExtras();
                String video_path = b.getString("selected_video");
                images_to_pass.add(video_path);
//               video[0] = video_path;
            }
            else if(imageslist.size()>0){
//                Uri myUri = Uri.parse(getIntent().getStringExtra("selected_image"));
                imageView.setImageURI(imageslist.get(0));
                images_to_pass = getImages(imageslist.get(0));
//               Log.e(TAG, images_to_pass.toString());
            }
        }
        getPrivacyTitles();

       // Log.d(TAG, "onCreate: got the chosen image: " + getIntent().getStringExtra("selected_image"));
        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });

        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                    //upload the image to database
                    showConfirmDialog();
            }
        });


    }

    private void getPrivacyTitles() {
//        progressDialog.setTitle("POSTS");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.PrivacyTitles,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");

                            if(msg.equals("Success")){
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                privacyID = new String[jsonArray.length()];
                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    privacyID[i] = jsonObject1.getString("PPID");
                                    spinnerArray.add(jsonObject1.getString("PrivacyTitle"));
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        NextActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                            }
                            else if(msg.equals("Error")){
                                Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Connection timeout error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "server couldn't find the authenticated request", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server is not responding", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "Your device is not connected to internet", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Parse Error (because of invalid json or xml)", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("POST");
        builder.setMessage("Are you sure you want to post ?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle b = getIntent().getExtras();
                if(b.getString("selected_video")!=null){
                    uploadVideoToServer(b.getString("selected_video"));
                }
                else {
                    if(text_to_pass.size()>0)
                        upload(UID, text_to_pass, "",privacyID[spinner.getSelectedItemPosition()]);
                    else if(images_to_pass.size()>0)
                        upload(UID, images_to_pass, textView.getText().toString(), privacyID[spinner.getSelectedItemPosition()]);
                    else
                        uploadVideoToServer(images_to_pass.get(0));
                }
            }
        });

        builder.create();
        builder.show();
    }

    private ArrayList<String> getImages(Uri imageslist){
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageslist);
                path = getStringImage(bitmap);
                list.add(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

        return list;
    }


    private void uploadVideoToServer(String pathToVideoFile){
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(NextActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                Toast.makeText(getApplicationContext(), "Video Uploaded!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), TabActivity.class));
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = null;
                try {
                    msg = u.uploadVideo(pathToVideoFile, UID, textView.getText().toString(), privacyID[spinner.getSelectedItemPosition()]);
                    Log.e(TAG, msg);
                    try {
                        JSONObject jsonObject = new JSONObject(msg);
                        String message = jsonObject.getString("Msg");
                        if(message.equals("Success")){
//                            Toast.makeText(getApplicationContext(), "Video Uploaded", Toast.LENGTH_SHORT).show();
                            //Start Public/Friend activity
//                            startActivity(new Intent(getApplicationContext(), TabActivity.class));
                        }
                        else if(message.equals("Error")){
                            String reason = jsonObject.getString("Reason");
                            if(reason.equals("NoUser"))
                                Toast.makeText(getApplicationContext(), "No User found", Toast.LENGTH_SHORT).show();
                            else if(reason.equals("File Size Limit Exceeds"))
                                Toast.makeText(getApplicationContext(), "File Size Limit Exceeds", Toast.LENGTH_SHORT).show();
//                            ProgressDialog.show(NextActivity.this, "Error", reason, false, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
//        Upload u = new Upload();
//        String msg = "";
//        try {
//            msg = u.uploadVideo(pathToVideoFile, UID);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return msg;
    }

    private void upload(final String UID, final ArrayList<String> images, final String caption,String scope){
        progressDialog.setTitle("POSTS");
        progressDialog.setMessage("Uploading posts, Please wait....");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Intent it = new Intent(getApplicationContext(), TabActivity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(it);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String msg = jsonObject.getString("Msg");
                           // Toast.makeText(getApplicationContext(), jsonObject.getString("Msg"), Toast.LENGTH_SHORT).show();
                            if(msg.equals("success")){
                                Toast.makeText(getApplicationContext(), "Post updated!", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(getApplicationContext(), TabActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                            else if(msg.equals("Error"))
                            {
                                String reason = jsonObject.getString("Reason");
                                if(reason.equals("NoMedia")){
                                    Toast.makeText(NextActivity.this, "No media", Toast.LENGTH_SHORT).show();
                                }
                                else if(reason.equals("NoUser")){
                                    Toast.makeText(getApplicationContext(), "No user", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Connection timeout error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "server couldn't find the authenticated request", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server is not responding", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "Your device is not connected to internet", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Parse Error (because of invalid json or xml)", Toast.LENGTH_SHORT).show();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID", UID);
                for (int i = 0; i < 1; i++)
                {
                    params.put("Dyna-UserPosts["+i+"]", images.get(0));
                }
                //params.put("Dyna-UserPosts", post_images.toString());
                if(!caption.equals("")){
                    params.put("Dyna-Caption", caption);
                }
                params.put("Dyna-PrivacyScope", scope);
//                params.put("name", "file");
//                params.put("filename", )
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
           0,
           DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
           DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String name = spinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
