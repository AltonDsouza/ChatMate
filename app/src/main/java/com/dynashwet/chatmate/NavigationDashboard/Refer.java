package com.dynashwet.chatmate.NavigationDashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.BuildConfig;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Refer extends AppCompatActivity implements View.OnClickListener {

    Button share;
    EditText code;
    String code1;
    String UID;
    private String url = AppConstant.getRefer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        share = findViewById(R.id.share);
        code = findViewById(R.id.referalcode);

        share.setOnClickListener(this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID","");

        getReferCode();
    }

    @Override
    public void onClick(View v) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ScrollStatus");
        String shareMessage= "\nMy Referral code\n";
        shareMessage = shareMessage +code.getText().toString()+"\n"+ " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "choose one"));
    }

    private void getReferCode(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("UserInfo"))
                            {
                                //RefferalCode
//                                Log.e("refer","inside");
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                for(int i = 0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    code1 = jsonObject1.getString("Referral");

                                }
                                code.setText(code1);


                            }
                            else if(msg.equals("Error"))
                            {
                                Toast.makeText(getApplicationContext(), "Error generating referral code, please try agian later!", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), ""+error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID",UID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}
