package com.briatka.pavol.closertostars;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.briatka.pavol.closertostars.fragments.GalleryFragment;
import com.briatka.pavol.closertostars.fragments.MasterPictureFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements CustomRecyclerViewAdapter.OnImageClickListener {


    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;

    private static int customSquareSize;

    private static String layoutTag;

    @BindView(R.id.main_view)
    LinearLayout mainViewLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        final ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


        if (activeNetwork == null || !activeNetwork.isConnected()) {
            Toast.makeText(this, getResources().getString(R.string.no_connection_msg),
                    Toast.LENGTH_LONG).show();
        }


        int getOrientation = getResources().getConfiguration().orientation;


        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        if (getOrientation == 1) {
            customSquareSize = screenWidth / 3;
        } else {
            customSquareSize = screenWidth / 6;
        }

        layoutTag = mainViewLinearLayout.getTag().toString();

        if (savedInstanceState == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();

            MasterPictureFragment mainFragment = new MasterPictureFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.master_picture_frame_layout, mainFragment)
                    .commit();

            GalleryFragment galleryFragment = new GalleryFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.gallery_frame_layout, galleryFragment)
                    .commit();
        }


    }

    public static int getCustomSize() {
        return customSquareSize;
    }

    public static String getLayoutTag() {
        return layoutTag;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu_bar, menu);

        final MenuItem menuItemRefresh = menu.findItem(R.id.action_refresh);
        menuItemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boolean isConnected = checkConnection();
                if (isConnected) {
                    GalleryFragment refreshedFragment = new GalleryFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.gallery_frame_layout, refreshedFragment)
                            .commit();
                } else {
                    Toast.makeText(getBaseContext(),getResources().getString(R.string.no_connection_msg),
                            Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        final MenuItem menuItemSettings = menu.findItem(R.id.action_settings);
        menuItemSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent openSettings = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(openSettings);
                return false;
            }
        });

        final MenuItem menuItemHelp = menu.findItem(R.id.action_help);
        menuItemHelp.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent openUserGuide = new Intent(getBaseContext(), UserGuideActivity.class);
                startActivity(openUserGuide);
                return false;
            }
        });
        return true;
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    @Override
    public void onImageClicked(ItemModel clickedImage) {

        MasterPictureFragment masterPictureFragment = new MasterPictureFragment();

        masterPictureFragment.setSelectedPicture(clickedImage);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.master_picture_frame_layout, masterPictureFragment)
                .commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.with_permission), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.without_permission),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
