package com.dynashwet.chatmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aws on 11/03/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context mContext;
    String UID;
    private List<ChatModel> mData;
    RequestOptions option;

    public ChatAdapter(Context mContext, List<ChatModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater inflater = LayoutInflater.from(mContext);

            view = inflater.inflate(R.layout.single_user_chat_design,parent,false) ;
            final MyViewHolder viewHolder = new MyViewHolder(view) ;
            return viewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.message.setText(mData.get(position).getMessage());
        holder.time.setText(mData.get(position).getTime());
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0);
        UID = pref.getString("UserID", "");
        if(mData.get(position).getUser_id().equals(UID))
        {
            holder.view_container.setBackgroundResource(R.drawable.single_user);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.view_container.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        holder.view_container.setOnClickListener(view -> {
            new AlertDialog.Builder(view.getRootView().getContext())
                    .setTitle("Delete?")
                    .setMessage("Delete this message?")
                    .setIcon(R.drawable.del)
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    })
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        //Delete and notify here
                        deleteSingleChat(mData.get(position).getMid(), position);
                    }).show();
        });


    }


    private void deleteSingleChat(String msgID, int position){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.DeleteChat,
                success-> {
                    try {
                        JSONObject jsonObject = new JSONObject(success);
                        if(jsonObject.getString("Msg").equals("Success"))
                        {

                            mData.remove(position);
                            notifyItemRemoved(position);
//                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            notifyItemRangeChanged(position,mData.size());
                        }
                        else {
//                            Toast.makeText(mContext, "No chats to delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                },
                error->{
                    Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    // progressDialog.dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", UID);//MsgID,UserID
                params.put("MsgID", msgID);
                return params;
            }
        };
        RequestHandler.getInstance(mContext).addToRequestQueue(stringRequest);
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends  RecyclerView.ViewHolder {

        TextView message,time;
        LinearLayout view_container;

        public MyViewHolder(View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.container);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);

        }

    }

}
