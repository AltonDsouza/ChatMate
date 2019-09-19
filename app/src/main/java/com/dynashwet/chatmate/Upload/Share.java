package com.dynashwet.chatmate.Upload;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.dynashwet.chatmate.Dashboard.ViewPagerAdapter;
import com.dynashwet.chatmate.R;


public class Share  extends AppCompatActivity{
    private ViewPager viewPager;

    private VideoActivity videoActivity;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_upload);
//
////        if(checkPermissionsArray(Permissions.PERMISSIONS)){
//
//
////        }
////        else {
//            verifyPermissions(Permissions.PERMISSIONS);
////        }
//    }



    public boolean checkPermissionsArray(String[] permissions){
        for(int i=0; i<permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission){
        int permissionRequest = ActivityCompat.checkSelfPermission(Share.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            //Permission was not granted
            return false;
        }
        else {
            //Permission was granted
            return true;
        }
    }

    public void verifyPermissions(String[] permissions){
        ActivityCompat.requestPermissions(
                (Activity) getApplicationContext(),
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
//        setUpViewPager();
    }
    /*
    return the current tab number
    * 0 = Gallery Fragment
    * 1 = PhotoActivity
     */
    public int getCurrentTabNumber(){
        return viewPager.getCurrentItem();
    }

    //Setup View Pager
    private void setUpViewPager(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        videoActivity = new VideoActivity();
//        viewPagerAdapter.addFragment(galleryActivity, "GALLERY");
//        viewPagerAdapter.addFragment(photoActivity, "PHOTO");
//        viewPagerAdapter.addFragment(videoActivity, "VIDEO");
        viewPager = (ViewPager) findViewById(R.id.containerViewPager);

        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tableBottom);
        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.getTabAt(0).setText(getString(R.string.Gallery));
//        tabLayout.getTabAt(1).setText(getString(R.string.Photo));
        tabLayout.getTabAt(2).setText(getString(R.string.string_video));
    }


}
