package com.example.pavol.closertostars.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pavol.closertostars.DetailActivity;
import com.example.pavol.closertostars.ItemModel;
import com.example.pavol.closertostars.MainActivity;
import com.example.pavol.closertostars.R;
import com.example.pavol.closertostars.asyncTasks.ReceiveMasterPictureTask;
import com.example.pavol.closertostars.interfaces.MasterPictureAsyncTaskCompleteListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MasterPictureFragment extends Fragment {

    @BindView(R.id.master_picture_image_view)
    ImageView masterPicture;
    @BindView(R.id.master_picture_title)
    TextView pictureTitle;
    @BindView(R.id.master_picture_card_view)
    CardView masterPictureCardView;
    @Nullable
    @BindView(R.id.master_picture_description)
    TextView masterPictureDescription;

    private ItemModel selectedPicture;
    private ItemModel savedObject;


    private String layoutTag;

    private static final String MASTER_OBJECT_KEY = "master_object";

    public MasterPictureFragment() {
    }

    public class ReceiveMasterPictureTaskCompleteListener implements MasterPictureAsyncTaskCompleteListener<ItemModel> {

        @Override
        public void onPictureReceived(ItemModel result) {

            if (result == null) {
                return;
            }

            savedObject = result;
            updateUi(result);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View masterPictureView = inflater.inflate(R.layout.master_picture_fragment, container, false);
        ButterKnife.bind(this, masterPictureView);

        layoutTag = MainActivity.getLayoutTag();


        if (savedInstanceState != null && savedInstanceState.getParcelable(MASTER_OBJECT_KEY) != null) {

            savedObject = savedInstanceState.getParcelable(MASTER_OBJECT_KEY);
            updateUi(savedObject);

        } else {
            if (selectedPicture != null) {

                savedObject = selectedPicture;
                updateUi(selectedPicture);

            } else {
                new ReceiveMasterPictureTask(getContext(), new ReceiveMasterPictureTaskCompleteListener()).execute();
            }
        }

        return masterPictureView;
    }

    public void setSelectedPicture(ItemModel passedObject) {
        selectedPicture = passedObject;
    }

    public void updateUi(final ItemModel result) {

        int getOrientation = getResources().getConfiguration().orientation;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        if (getOrientation == 1) {
            masterPicture.setMaxHeight(screenHeight / 3);
        } else {
            int recalculatedValue = (screenHeight / 100) * 60;
            masterPicture.setMaxHeight(recalculatedValue);
        }


        pictureTitle.setText(result.mTitle);

        if (layoutTag.equals(getResources().getString(R.string.tablet_landscape_layout_tag))
                || layoutTag.equals(getResources().getString(R.string.tablet_portrait_layout_tag))) {
            masterPictureDescription.setText(result.mDescription);
        }


        if (result.mMediaType.equals("video")) {
            Picasso.with(getContext())
                    .load(R.drawable.videoplaceholder)
                    .placeholder(R.drawable.skyvector)
                    .into(masterPicture);
        } else {
            Picasso.with(getContext())
                    .load(result.mMediaSource)
                    .placeholder(R.drawable.skyvector)
                    .into(masterPicture);

        }


        masterPictureCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((layoutTag.equals(getResources().getString(R.string.tablet_portrait_layout_tag))
                        || layoutTag.equals(getResources().getString(R.string.tablet_landscape_layout_tag))) && result.mMediaType.equals("video")) {
                    openVideoInBrowser(result.mMediaSource);
                } else {

                    openDetailActivity(result);
                }
            }
        });


    }

    public void openDetailActivity(ItemModel objectDetails) {
        Intent objectDetailsData = new Intent(getContext(), DetailActivity.class);
        objectDetailsData.putExtra(DetailActivity.RECEIVED_OBJECT_DATA_KEY, objectDetails);
        objectDetailsData.putExtra(DetailActivity.LAYOUT_TAG_KEY, layoutTag);
        startActivity(objectDetailsData);
    }

    private void openVideoInBrowser(String videoUrl) {
        Intent openActivity = new Intent(Intent.ACTION_VIEW);
        openActivity.setData(Uri.parse(videoUrl));

        if (openActivity.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(openActivity);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MASTER_OBJECT_KEY, savedObject);
    }
}
