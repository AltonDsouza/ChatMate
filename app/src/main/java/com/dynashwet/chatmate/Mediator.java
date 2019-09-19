package com.dynashwet.chatmate;
/*
Author:    Alton Dsouza
 */
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.dynashwet.chatmate.Utils.AppConstant;
import com.dynashwet.chatmate.Utils.VolleyCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Mediator {
    private Context context;

    public Mediator(Context context) {
        this.context = context;
    }

    public void LikeDisCon(final String UID, final String Post_ID, final String state, final VolleyCallBack volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.LikeDis,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            volleyCallBack.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

             errorStuff(error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("LikeBy", UID);
                params.put("PostID", Post_ID);
                params.put("State", state);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }


    public void reportPost(final String postID, final String reportMessage, final String UID, final VolleyCallBack volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.REPORT_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            volleyCallBack.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               errorStuff(error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pid", postID);
                params.put("ReportMessage", reportMessage);
                params.put("ReportBy", UID);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }


    public void getContactData(final String UID, final ArrayList<String> contacts, final VolleyCallBack volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.FRIENDS_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            volleyCallBack.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               errorStuff(error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID", UID);
                Log.e("TestUID",UID);
                int Limits = (contacts.size() > 1000) ? 1000: contacts.size();
                for (int i = 0; i < Limits; i++)//contacts.size
                {
                    params.put("contact["+i+"]", contacts.get(i));
                }

                return params;
            }
        };
       RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void sendFriendRequest(String PostID, String UID, VolleyCallBack volleyCallBack){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.SendFriendRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            volleyCallBack.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               errorStuff(error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("PostID", PostID);
                params.put("FollowBy", UID);
                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }


    public void delComment(String comID, String postID, String UID, VolleyCallBack volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.DelComments,
                success-> {
                    try {
                        volleyCallBack.onSuccess(success);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error->{
                    errorStuff(error);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID", UID);
                params.put("Dyna-PostID", postID);
                params.put("Dyna-ComID", comID);
                return params;

            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }















    public void errorStuff(VolleyError error){
        //Just learning to refactor, don't judge my code!!!
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(context, "Connection timeout error", Toast.LENGTH_SHORT).show();

        } else if (error instanceof AuthFailureError) {
            Toast.makeText(context, "server couldn't find the authenticated request", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            Toast.makeText(context, "Server is not responding", Toast.LENGTH_SHORT).show();
        } else if (error instanceof NetworkError) {
            Toast.makeText(context, "Your device is not connected to internet", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError) {
            Toast.makeText(context, "Parse Error (because of invalid json or xml)", Toast.LENGTH_SHORT).show();
        }
    }

}
