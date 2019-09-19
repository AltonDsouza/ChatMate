package com.dynashwet.chatmate.Dashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dynashwet.chatmate.Adapter.NotificationAdapter;
import com.dynashwet.chatmate.Models.Notification;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.RequestHandler;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationFragment extends Fragment  {

    private static final String TAG = "NotificationFragment";
    private boolean _hasLoadedOnce = false;
//    private FloatingActionButton browse_contacts;
    private ListView notificationList;
    private EditText input;
    private String UID;
    private ProgressDialog progressDialog;
    private List<Notification> notifications ;

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_search, container, false);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        notificationList = view.findViewById(R.id.searchList);
        input = view.findViewById(R.id.inputSearch);
        progressDialog = new ProgressDialog(getContext());
        notifications = new ArrayList<>();

        input.setVisibility(View.GONE);

        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");
        getNoti(UID);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//        if(this.isVisible()){
//            if(isVisibleToUser && !_hasLoadedOnce){
//                //loadData
//                _hasLoadedOnce = true;
//            }
//        }
//    }

    private void getNoti(String UID){
       // progressDialog.setMessage("Loading...");
       // progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GetNoti,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
//                                Toast.makeText(getActivity(), "Blocked User!", Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String title = jsonObject1.getString("Title");
                                    String message = jsonObject1.getString("Message");
                                    String fromID = jsonObject1.getString("FromUID");
                                    String name = jsonObject1.getString("Name");
                                    String image = AppConstant.BASE_URL+jsonObject1.getString("ProfilePic");


                                    JSONObject payload = jsonObject1.getJSONObject("Payload");
                                    String postID = payload.getString("Post");

//                                    if(postID!=null){
//                                        notifications.add(new Notification(title, message, image, postID));
//                                    }

                                    notifications.add(new Notification(title, message, image, fromID, name, postID));
                                }
                                NotificationAdapter notificationAdapter = new NotificationAdapter(getContext(), notifications, TAG);
                                notificationList.setAdapter(notificationAdapter);
                            }
                            else {
                                Toast.makeText(getActivity(), "No notifications", Toast.LENGTH_SHORT).show();
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
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UID", UID);
//                params.put("pid", BlockedUser);
                return params;
            }
        };

        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}
