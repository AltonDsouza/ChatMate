package com.dynashwet.chatmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
import com.dynashwet.chatmate.Adapter.MyVideosAdapter;
import com.dynashwet.chatmate.Dashboard.Setting;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {
    private List<ChatModel> lstAnime;
    EditText message;
    ImageView send, chatOverflow;
    CircleImageView userimage;
    TextView friendname;
    Handler mHandler;
    String UID, from, to;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    RequestOptions option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

//        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().hide();
        lstAnime = new ArrayList<>() ;
        recyclerView = findViewById(R.id.recyclerviewid);
        progressBar = findViewById(R.id.progress);
        userimage = findViewById(R.id.userimage);
        friendname = findViewById(R.id.friendname);
        chatOverflow = findViewById(R.id.chatOverflow);
        option=new RequestOptions().fitCenter().placeholder(R.drawable.defaultcomment).error(R.drawable.defaultcomment);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");
       // Toast.makeText(getApplicationContext(),getIntent().getStringExtra("pid"),Toast.LENGTH_SHORT).show();
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(this).load(getIntent().getStringExtra("dp")).apply(option).into(userimage);
        friendname.setText(getIntent().getStringExtra("name"));
        this.mHandler = new Handler();
        m_Runnable.run();

        if(getIntent().getStringExtra("from")!=null && getIntent().getStringExtra("to")!=null ){
            from = getIntent().getStringExtra("from");
            to = getIntent().getStringExtra("to");
            getMessages(from, to);
        }
        else if(getIntent().getStringExtra("friendid")!=null){
            from = UID;
            to = getIntent().getStringExtra("friendid");
            getMessages(from, to);
        }




        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!message.getText().toString().equals(""))
                {
                    sendMessage();
                }

            }
        });

        PopupMenu popup = new PopupMenu(this, chatOverflow);
        //inflating menu from xml resource
        popup.inflate(R.menu.menu_chat);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_clear:
                        //Clear Chat API here
                        clearChat();
                        break;

                }
                return false;
            }
        });

        chatOverflow.setOnClickListener(view -> {
            popup.show();
        });
    }


    private void clearChat(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.ClearChat,
               success-> {
                        try {
                            JSONObject jsonObject = new JSONObject(success);
                            if(jsonObject.getString("Msg").equals("Success"))
                            {
                                getMessages(from, to);
                                message.setText("");
                            }
                            else {
                                Toast.makeText(this, "No chats to delete", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(this, "catch", Toast.LENGTH_SHORT).show();
                        }

                },
                error->{
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        // progressDialog.dismiss();

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID", UID);
                return params;

            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }


    private void sendMessage() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://topautocareindia.com/ChatModule/Chat/AddChat",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
//                            Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                            if(jsonObject.getString("msg").equals("success"))
                            {
                                getMessages(from, to);
                                message.setText("");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        // progressDialog.dismiss();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fromid", UID);
                params.put("toid", to);
                params.put("msg",message.getText().toString().trim());
                return params;

            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    public  void  getMessages(String UID, String to)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://topautocareindia.com/ChatModule/Chat/ViewChat",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);

                            JSONArray jsonDevices = obj.getJSONArray("chats");

                               lstAnime.clear();
                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    ChatModel anime = new ChatModel();
                                    anime.setUser_id(d.getString("FromID"));
                                    anime.setMessage(d.getString("Msg"));
                                    anime.setMid(d.getString("MsgID"));
                                    String time[]=d.getString("MsgAt").split(" ");
                                    String time1[]=time[1].split(":");
                                    anime.setTime(time1[0]+":"+time1[1]);
                                    lstAnime.add(anime);


                                }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ChatAdapter myadapter = new ChatAdapter(getApplicationContext(),lstAnime) ;
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.scrollToPosition(myadapter.getItemCount()-1);
                        recyclerView.setAdapter(myadapter);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // replace userid and friend id here
                params.put("fromid",UID);
                params.put("toid",to);
                return params;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }





    private final Runnable m_Runnable = new Runnable()
    {
        public void run()
        {
           // Toast.makeText(Chat.this,"in runnable",Toast.LENGTH_SHORT).show();
            getMessages(from, to);
            Chat.this.mHandler.postDelayed(m_Runnable,2000);
        }

    };
}
