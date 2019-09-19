package com.dynashwet.chatmate.Dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.dynashwet.chatmate.Activity.NewFriendPost;
import com.dynashwet.chatmate.Activity.NewPublicPost;
import com.dynashwet.chatmate.Credential.LoginActivity;
import com.dynashwet.chatmate.Fragments.Pager;
import com.dynashwet.chatmate.NavigationDashboard.BlockedList;
import com.dynashwet.chatmate.NavigationDashboard.FriendList;
import com.dynashwet.chatmate.NavigationDashboard.Privacy;
import com.dynashwet.chatmate.NavigationDashboard.ProfileActivity;
import com.dynashwet.chatmate.NavigationDashboard.Refer;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.Permissions;

public class TabActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    //viewPager
    private ViewPager viewPager;

    ViewPagerAdapter adapter;
    private NavigationView navigationView;

    //Fragments
    NotificationFragment notificationFragment;
    NewPublicPost publicFragment;
    NewFriendPost friendsFragment;
    SearchFragment searchFragment;
    ProfileActivity profileActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Pager  adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        setupViewPager(viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setSaveFromParentEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(TabActivity.this);

        //Verify permissions
        verifyPermissions(Permissions.PERMISSIONS);

//        hideItem();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //Send Token to database
        //API call here


    }


    public void verifyPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(
                this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    private void hideItem()
    {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.action_block).setVisible(false);
        nav_Menu.findItem(R.id.action_refer).setVisible(false);
    }

//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_home, menu);
//        // Associate searchable configuration with the SearchView
////        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
////        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
////        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//
////            case R.id.action_profile:
////                //Navigate to edit_profile
////                startActivity(new Intent(getApplicationContext(), EditProfile.class));
////                return true;
//            case R.id.action_settings:
////                Toast.makeText(this, "Home Settings Click", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getApplicationContext(), Setting.class));
//                return true;
////            case R.id.action_logout:
////
//
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    private void setupViewPager(ViewPager viewPager)
//    {
//        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        publicFragment=new NewPublicPost();//Modified
//        friendsFragment=new NewFriendPost();//Modified
//        notificationFragment =new NotificationFragment();
//        searchFragment = new SearchFragment();
////        notificationFragment = new SearchFragment();
//        adapter.addFragment(publicFragment,"PUBLIC");
//        adapter.addFragment(friendsFragment,"FRIENDS");
//        adapter.addFragment(notificationFragment,"NOTIFICATION");
//        adapter.addFragment(searchFragment, "SEARCH");
////        adapter.addFragment(notificationFragment,"NOTIFICATION");
////        adapter.addFragment(profileActivity, "PROFILE");
//        viewPager.setAdapter(adapter);
//    }

//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_logout:
                SharedPreferences pref=getApplicationContext().getSharedPreferences("MyPref",0);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                Toast.makeText(getApplicationContext(), "Logout Successfully", Toast.LENGTH_LONG).show();
                break;

            case R.id.action_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;

            case R.id.action_requests:
                startActivity(new Intent(getApplicationContext(), FriendList.class));
                break;
            case R.id.action_block:
                startActivity(new Intent(getApplicationContext(), BlockedList.class));
                break;

            case R.id.action_refer:
                startActivity(new Intent(getApplicationContext(), Refer.class));
                break;

            case R.id.action_privacy:
                startActivity(new Intent(getApplicationContext(), Privacy.class));
                break;
        }
        return false;
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



}
