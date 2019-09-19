package com.dynashwet.chatmate.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dynashwet.chatmate.Activity.NewFriendPost;
import com.dynashwet.chatmate.Activity.NewPublicPost;
import com.dynashwet.chatmate.Dashboard.NotificationFragment;
import com.dynashwet.chatmate.Dashboard.SearchFragment;

/**
 * Created by Belal on 2/3/2016.
 */
//Extending FragmentStatePagerAdapter
public class Pager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                NewPublicPost tab1 = new NewPublicPost();
                return tab1;

            case 1:
                NotificationFragment tab2 = new NotificationFragment();
                return tab2;

            case 2:
                SearchFragment tab3 = new SearchFragment();
                return tab3;

            case 3:
                NewFriendPost tab4 = new NewFriendPost();
                return tab4;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}