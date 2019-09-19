package com.dynashwet.chatmate.Credential;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.dynashwet.chatmate.Utils.AppConstant;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnterProfile extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView circleImageView;
    private EditText fullname, location;
    private Button submit;
    private String PhoneNo, Password, path, UID, token;
    private ProgressDialog progressDialog;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_profile);
        fVBID();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        PhoneNo = pref.getString("PhoneNo", "");
        Password = pref.getString("PassWord","");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyToken",0);
        token = preferences.getString("Token", "");

    }

    private void fVBID() {
        circleImageView = (CircleImageView)findViewById(R.id.profile_image);
        fullname = findViewById(R.id.Name);
        location = findViewById(R.id.location);
        submit = (Button)findViewById(R.id.submit);
        progressDialog = new ProgressDialog(this);

        circleImageView.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == circleImageView){
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        else if(v == submit){

            if(fullname.getText().toString().length()==0 ){//|| fullname.getText().toString().length()<10){
                fullname.setError("Name should not be empty!");
                fullname.requestFocus();
            }
            else if(location.getText().toString().length()==0){
                location.setError("Location cannot be empty!");
                location.requestFocus();
            }
//            else if(path == null){
//                Toast.makeText(this, "Please upload an image!", Toast.LENGTH_SHORT).show();
//            }
            else {
                saveProfileDetails(PhoneNo, Password, fullname.getText().toString(), location.getText().toString(), path, token);
            }
        }
    }

   public void saveProfileDetails(final String contact, final String password, final String username, final String location, final String image, final String token){
        progressDialog.setMessage("Uploading your details...");
        progressDialog.setTitle("New User");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){

                                Toast toast = Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                UID = jsonObject.getString("User");

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("LogIN", "1");
                                editor.putString("UserID", UID);

                                editor.commit();

                                startActivity(new Intent(getApplicationContext(), TabActivity.class));
                                finish();
                            }
                            else if(msg.equals("Exist")){
                                Toast toast = Toast.makeText(getApplicationContext(), "Already Exists!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Connection has timed out", Toast.LENGTH_SHORT).show();

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
                params.put("Contact", contact);
                params.put("Passwd", password);
                params.put("Name", username);
                params.put("Address", location);
                params.put("ProfilePic", image);
                params.put("FirebaseToken",token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

       stringRequest.setRetryPolicy(new DefaultRetryPolicy(
               0,
               DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
            Uri uri = result.getUri();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            path = getStringImage(bitmap);
            Log.e("path", path);
            circleImageView.setImageBitmap(bitmap);
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
