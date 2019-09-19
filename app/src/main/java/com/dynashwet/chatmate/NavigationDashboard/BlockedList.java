package com.dynashwet.chatmate.NavigationDashboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.dynashwet.chatmate.Models.Block;
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

import de.hdodenhof.circleimageview.CircleImageView;

/*
Author:    Alton Dsouza
 */
public class BlockedList extends AppCompatActivity {

    private ListView listView;
    private ProgressDialog progressDialog;
    private List<Block> list = new ArrayList<>();
    private String UID;
    private static String BlockedUserID [];
    private BlockListAdapter blockListAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_list);
        listView = findViewById(R.id.blockedList);
        progressDialog = new ProgressDialog(this);

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");

        populateListView();
    }

    private void unBlock(String UID, String UploadBy, int position){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.UNBLOCK_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                list.remove(position);
                                blockListAdapter.notifyDataSetChanged();
//                                Snackbar snackbar = Snackbar
//                                        .make(linearLayout, "New friend added to your list", Snackbar.LENGTH_LONG);
//                                snackbar.show();
                                Toast.makeText(getApplicationContext(), "Unblocked User!", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Could'nt Unblock user", Toast.LENGTH_SHORT).show();
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
                params.put("Dyna-BlockedBy", UID);
//                params.put("pid", BlockedUser);
                params.put("Dyna-BlockedUser", UploadBy);
                params.put("Dyna-BlockStatus", "0");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void populateListView(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.BlockedList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                BlockedUserID = new String[jsonArray.length()];
                                String name = "", pic = "";
                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    name = jsonObject1.getString("Name");
                                    pic = AppConstant.BASE_URL+jsonObject1.getString("ProfilePic");
                                    BlockedUserID[i] = jsonObject1.getString("BlockedUser");
                                    list.add(new Block(name, pic));
                                }
                                blockListAdapter = new BlockListAdapter(list, getApplicationContext());
                                listView.setAdapter(blockListAdapter);
                            }
                            else if(msg.equals("Error")){
                                Toast.makeText(getApplicationContext(), "No Users Blocked!", Toast.LENGTH_SHORT).show();
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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public class BlockListAdapter extends BaseAdapter{
        private List<Block> blockList;
        private Context context;
        RequestOptions option;

        public BlockListAdapter(List<Block> blockList, Context context) {
            this.blockList = blockList;
            this.context = context;

            option=new RequestOptions().fitCenter().placeholder(R.drawable.defaultcomment).error(R.drawable.defaultcomment);
        }

        @Override
        public int getCount() {
            return blockList.size();
        }

        @Override
        public Object getItem(int position) {
            return blockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.block_item, viewGroup, false);
            CircleImageView circleImageView = view.findViewById(R.id.blocked_profile);
            TextView textView = view.findViewById(R.id.blocked_name);
            Button unblock = view.findViewById(R.id.unblock);
            Glide.with(context).load(blockList.get(position).getImage()).apply(option).into(circleImageView);
            //Picasso.with(context).load(blockList.get(position).getImage()).into(circleImageView);
            textView.setText(blockList.get(position).getName());
            unblock.setOnClickListener(click ->{

                AlertDialog.Builder builder = new AlertDialog.Builder(BlockedList.this);
                builder.setTitle("Unblock");
                builder.setMessage("Unblock User");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Call API
                        unBlock(UID, BlockedUserID[position], position);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
            return view;
        }
    }
}
