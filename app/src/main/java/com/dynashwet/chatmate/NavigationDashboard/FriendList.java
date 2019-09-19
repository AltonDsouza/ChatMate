package com.dynashwet.chatmate.NavigationDashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.dynashwet.chatmate.Models.Friends;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendList extends AppCompatActivity {

    private ListView listView;
    private ImageView imageView;
    private TextView reqmsg, submsg;
    private ProgressDialog progressDialog;
    private FriendsAdapter friendsAdapter;
    private List<Friends> friendsList = new ArrayList<>();
    private String UID;
    private String [] FID;
    private String [] FollowBy;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_request);
        listView = findViewById(R.id.friendList);
        linearLayout = findViewById(R.id.friendLinear);
        imageView = findViewById(R.id.noReqImage);
        reqmsg = findViewById(R.id.noReqMessage);
        submsg = findViewById(R.id.noReqSubMessage);
        progressDialog = new ProgressDialog(this);
        SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");

        getFriendRequests();
    }

    private void getFriendRequests(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.getFriendRequests,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                JSONArray jsonArray = jsonObject.getJSONArray("RequestedData");
                                FID = new String[jsonArray.length()];
                                FollowBy = new String[jsonArray.length()];
                                for(int i=0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String name = jsonObject1.getString("Name");
                                    String proPic = AppConstant.BASE_URL+jsonObject1.getString("ProfilePic");
                                    FID[i] = jsonObject1.getString("FID");
                                    FollowBy[i] = jsonObject1.getString("FollowBy");
                                    friendsList.add(new Friends(name, proPic));
                                }

                                friendsAdapter = new FriendsAdapter(getApplicationContext(), friendsList);
                                listView.setAdapter(friendsAdapter);
                            }
                            else if(msg.equals("Error")) {
                                    listView.setVisibility(View.GONE);
                                    imageView.setVisibility(View.VISIBLE);
                                    reqmsg.setVisibility(View.VISIBLE);
                                    submsg.setVisibility(View.VISIBLE);
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
                params.put("uid", UID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void accept(int position){
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.RequestResponse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                friendsList.remove(position);
                                friendsAdapter.notifyDataSetChanged();
                                Snackbar snackbar = Snackbar
                                        .make(linearLayout, "New friend added to your list", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                //Send Notification
                            }
                            else if(msg.equals("Miss Params")){

                            }
                            else {

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
                params.put("Dyna-FID", FID[position]);
                params.put("Dyna-UserID", UID);
                params.put("Dyna-FrndID", FollowBy[position]);
                params.put("Dyna-Status", "1");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void reject(int position){
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.RequestResponse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                friendsList.remove(position);
                                friendsAdapter.notifyDataSetChanged();
                                Snackbar snackbar = Snackbar
                                        .make(linearLayout, "Rejected!", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                //Send Notification
                            }
                            else if(msg.equals("Miss Params")){

                            }
                            else {

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
                params.put("Dyna-FID", FID[position]);
                params.put("Dyna-UserID", UID);
                params.put("Dyna-FrndID", FollowBy[position]);
                params.put("Dyna-Status", "0");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public class FriendsAdapter extends BaseAdapter{
        private Context context;
        private List<Friends> friendsList;
        RequestOptions option;
        public FriendsAdapter(Context context, List<Friends> friendsList) {
            this.context = context;
            this.friendsList = friendsList;
            option=new RequestOptions().fitCenter().placeholder(R.drawable.defaultcomment).error(R.drawable.defaultcomment);

        }

        @Override
        public int getCount() {
            return friendsList.size();
        }

        @Override
        public Object getItem(int position) {
            return friendsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.friend_list_item, viewGroup, false);
            CircleImageView circleImageView = view.findViewById(R.id.friendProfileImage);
            TextView name = view.findViewById(R.id.friendName);
            Button accept = view.findViewById(R.id.accept);
            Button reject = view.findViewById(R.id.reject);
            Glide.with(context).load(friendsList.get(position).getImage()).apply(option).into(circleImageView);
           // Picasso.with(context).load(friendsList.get(position).getImage()).into(circleImageView);
            name.setText(friendsList.get(position).getName());
            accept.setOnClickListener(click->{
//                Toast.makeText(context, position+"clicked", Toast.LENGTH_SHORT).show();
                accept(position);
            });

            reject.setOnClickListener(click ->{
//                Toast.makeText(context, position+"clicked", Toast.LENGTH_SHORT).show();
                reject(position);
            });
            return view;
        }
    }
}
