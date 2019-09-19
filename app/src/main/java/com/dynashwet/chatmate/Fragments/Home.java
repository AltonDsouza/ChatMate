package com.dynashwet.chatmate.Fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dynashwet.chatmate.R;

//Implementing the interface OnTabSelectedListener to our MainActivity
//This interface would help in swiping views
public class Home extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        //Adding toolbar to the activity
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        //Adding the tabs using addTab() method
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_public_black_24dp));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_group_black_24dp));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_notifications_none_black_24dp));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.search));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
