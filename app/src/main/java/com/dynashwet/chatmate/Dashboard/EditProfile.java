package com.dynashwet.chatmate.Dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.NavigationDashboard.ProfileActivity;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.RequestHandler;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private ImageView action_edit_profile, closeShare;
    private CircleImageView profile_image;
    private TextView next;
    private EditText fullname, city;
    private Button submit;
    private String path, UID, postID, contact, Name;
    private ProgressDialog progressDialog;
    private static final int RESULT_LOAD_IMAGE = 9;
    private static final String TAG = "EditProfile";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_profile);
        Log.e(TAG, "onCreate: Started Edit Profile Activity");

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");
        Name = pref.getString("Name", "");
        contact = pref.getString("contact", "");

        fVBID();

    }

    private void fVBID() {

        profile_image = findViewById(R.id.profile_image);
        submit = findViewById(R.id.submit);
        fullname =  findViewById(R.id.Name);
        city = findViewById(R.id.location);
        progressDialog = new ProgressDialog(this);

        submit.setText("Upload");
        fullname.setVisibility(View.GONE);
        city.setVisibility(View.GONE);

        profile_image.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == submit){
//            Log.e(TAG, path);
            if(path==null){
//                upload_details(UID, name.getText().toString());
                Toast.makeText(this, "Select an image to upload", Toast.LENGTH_SHORT).show();
            }
            else {
                //upload details
                upload_image(UID, path);

            }

        }

        else if(v == profile_image){
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            CropImage.activity(selectedImage)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        //If no image uploaded
        else {

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri uri = null;
            try{ uri = result.getUri();}catch (NullPointerException ex){ex.printStackTrace();}
            
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException ex){
                ex.printStackTrace();
            }
            path = getStringImage(bitmap);
           // Log.e("path", path);
            if(bitmap==null){
                Picasso.with(this).load(R.drawable.defaultcomment).into(profile_image);
            }
            else {
                profile_image.setImageBitmap(bitmap);
            }

        }
    }

    public String getStringImage(Bitmap bmp) {
        if(bmp==null){
            return "";
        }
        else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;

        }
    }

    private void upload_image(final String UID, final String path){
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.UPDATE_PROFILE_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(TAG, jsonObject.toString());
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                Toast.makeText(getApplicationContext(), "Updated profile.", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(i);
                            }
                            else if(msg.equals("MissParam")){
                                Toast.makeText(getApplicationContext(), "No Image", Toast.LENGTH_SHORT).show();
                            }
                            else if(msg.equals("Error")){
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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
                params.put("Dyna-UserID", UID);
                params.put("ProfilePic", path);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void upload_details(final String UID, final String name){
        progressDialog.setMessage("Updating, Please wait...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.UPDATE_PROFILE_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                Toast.makeText(getApplicationContext(), "Updated profile.", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(getApplicationContext(), TabActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
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
                params.put("Dyna-UserID", UID);
                params.put("Dyna-UserName", name);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void getPostData(final String postID){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_POST_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("SUCCESS")){
                                JSONObject jsonArray = jsonObject.getJSONObject("data");


//                                    name.setText(jsonArray.getString("Name"));
//                                    phone.setText(jsonArray.getString("Contact"));
                                    Picasso.with(getApplicationContext()).load(AppConstant.BASE_URL+
                                            jsonArray.getString("ProfilePic")).into(profile_image);

                            }
                            else if(msg.equals("MissParam")){
                                Toast.makeText(getApplicationContext(), "No PostID", Toast.LENGTH_SHORT).show();
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
                params.put("PostID", postID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    }





