package com.example.pavol.closertostars;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String RECEIVED_OBJECT_DATA_KEY = "received_object";
    public static final String LAYOUT_TAG_KEY = "layout_tag";

    private static final String SAVED_OBJECT_KEY = "saved_object";
    private static final String SAVED_LAYOUT_TAG_KEY = "saved_layout_tag";

    private ItemModel detailedObject;
    private static String videoKey;
    private BottomSheetBehavior bottomSheetBehavior;
    private String layoutTag;

    @Nullable
    @BindView(R.id.title)
    TextView pictureTitle;
    @Nullable
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.picture_of_day_detail)
    ImageView pictureDetail;
    @Nullable
    @BindView(R.id.play_button)
    Button playButton;
    @Nullable
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);


        if (savedInstanceState != null) {
            detailedObject = savedInstanceState.getParcelable(SAVED_OBJECT_KEY);
            layoutTag = savedInstanceState.getString(SAVED_LAYOUT_TAG_KEY);
        } else {

            Intent inheritedData = getIntent();
            layoutTag = inheritedData.getStringExtra(LAYOUT_TAG_KEY);
            detailedObject = inheritedData.getParcelableExtra(RECEIVED_OBJECT_DATA_KEY);
        }
        // when the app runs on a mobile phone it uses the bottom sheet behaviour
        if (layoutTag.equals(getResources().getString(R.string.portrait_layout_tag))
                || layoutTag.equals(getResources().getString(R.string.landscape_layout_tag))) {


            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            pictureTitle.setText(detailedObject.mTitle);
            description.setText(detailedObject.mDescription);
            setTitle(detailedObject.mTitle);

            if (detailedObject.mMediaType.equals("video")) {
                playButton.setVisibility(View.VISIBLE);

                final String videoUrl = videoUrlMethod(detailedObject.mMediaSource);
                pictureDetail.setImageResource(R.drawable.videoplaceholder);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (videoUrl.contains("http")) {
                            openVideoInBrowser(videoUrl);
                        } else if (!videoUrl.contains("http")) {
                            videoKey = videoUrl;
                            openVideoActivity();
                        }
                    }
                });
            } else {
                playButton.setVisibility(View.GONE);
                Picasso.with(this)
                        .load(detailedObject.mMediaSource)
                        .into(pictureDetail);
            }

            pictureDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (layoutTag.equals(getResources().getString(R.string.portrait_layout_tag))
                            || layoutTag.equals(getResources().getString(R.string.landscape_layout_tag))) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    fullscreenMode();
                }
            });

            //when the app runs on a tablet it shows only the picture because the
            // description is showed in the main activity. I do not have to deal with video content
            //because this is handled from the main activity as well.
        } else {
            playButton.setVisibility(View.GONE);
            Picasso.with(this)
                    .load(detailedObject.mMediaSource)
                    .into(pictureDetail);

            pictureDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fullscreenMode();
                }
            });
        }


    }

    private void openVideoActivity() {
        Intent openActivity = new Intent(this, VideoActivity.class);
        this.startActivity(openActivity);
    }

    private void openVideoInBrowser(String videoUrl) {
        Intent openActivity = new Intent(Intent.ACTION_VIEW);
        openActivity.setData(Uri.parse(videoUrl));

        if (openActivity.resolveActivity(getPackageManager()) != null) {
            startActivity(openActivity);
        }
    }

    private String videoUrlMethod(String unprocessedUrl) {
        String videoUrl;
        if (unprocessedUrl.contains("youtube")) {
            String[] separated = unprocessedUrl.split("embed/");
            if (separated[1].contains("?")) {
                String[] secondSplit = separated[1].split("\\?");
                videoUrl = secondSplit[0];
            } else {
                videoUrl = separated[1];
            }
        } else {
            videoUrl = unprocessedUrl;
        }
        return videoUrl;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        final MenuItem menuItemShare = menu.findItem(R.id.action_share);
        menuItemShare.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                menuItem.setIntent(shareIntent());
                return false;
            }
        });

        final MenuItem menuItemDownload = menu.findItem(R.id.action_download);
        if (detailedObject.mMediaType.equals("video")) {
            menuItemDownload.setVisible(false);
        }
        menuItemDownload.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                downloadFile(detailedObject.mMediaSource);
                return false;
            }
        });

        final MenuItem menuItemDescription = menu.findItem(R.id.action_description);
        if (layoutTag.equals(getResources().getString(R.string.tablet_portrait_layout_tag))
                || layoutTag.equals(getResources().getString(R.string.tablet_landscape_layout_tag))) {
            menuItemDescription.setVisible(false);
        }
        menuItemDescription.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (layoutTag.equals(getResources().getString(R.string.portrait_layout_tag))
                        || layoutTag.equals(getResources().getString(R.string.landscape_layout_tag))) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                return false;
            }
        });


        return true;
    }

    private Intent shareIntent() {

        String mimeType = "text/plain";

        String chooser = "Share content";


        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType(mimeType)
                .setChooserTitle(chooser)
                .setText(detailedObject.mMediaSource)
                .createChooserIntent();
        return shareIntent;

    }

    public void downloadFile(String url) {

        String fileNameDate;

        fileNameDate = detailedObject.mDate;

        File fileDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath());

        boolean wasSuccessful = fileDir.mkdirs();
        if (!wasSuccessful) {
            Log.e("Error:", "Directory not created");
        }


        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming((true))
                .setDescription("Downloading menu_bar from NASA")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir("/Closer to stars", fileNameDate + ".jpg");

        manager.enqueue(request);

    }

    public static String getVideoKey() {
        return videoKey;
    }

    private void fullscreenMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_OBJECT_KEY, detailedObject);
        outState.putString(SAVED_LAYOUT_TAG_KEY, layoutTag);
    }
}
