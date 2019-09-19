package com.dynashwet.chatmate;

/**
 * Created by rahul on 23/04/2019.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.dynashwet.chatmate.Dashboard.PublicFragment;

public class FullScreenMediaController extends MediaController {

    private ImageButton fullScreen;
    private String isFullScreen;

    public FullScreenMediaController(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {

        super.setAnchorView(view);

        //image button for full screen to be added to media controller
        fullScreen = new ImageButton (super.getContext());

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 80;
        addView(fullScreen, params);

        //fullscreen indicator from intent
        isFullScreen =  ((Activity)getContext()).getIntent().
                getStringExtra("fullScreenInd");

        if("y".equals(isFullScreen)){
            fullScreen.setImageResource(R.drawable.fullscreen);
        }else{
            fullScreen.setImageResource(R.drawable.ic_fullscreen_exit_black_24dp);
        }

        //add listener to image button to handle full screen and exit full screen events
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), PublicFragment.class);

                if("y".equals(isFullScreen)){
                    intent.putExtra("fullScreenInd", "");
                }
                else
                {
                    intent.putExtra("fullScreenInd", "y");
                }
                ((Activity)getContext()).startActivity(intent);
            }
        });
    }
}
