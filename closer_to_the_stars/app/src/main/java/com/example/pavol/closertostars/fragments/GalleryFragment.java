package com.example.pavol.closertostars.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pavol.closertostars.CustomRecyclerViewAdapter;
import com.example.pavol.closertostars.ItemModel;
import com.example.pavol.closertostars.R;
import com.example.pavol.closertostars.asyncTasks.ReceiveGalleryPicturesTask;
import com.example.pavol.closertostars.interfaces.AsyncTaskCompleteListener;
import com.github.ybq.android.spinkit.SpinKitView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String nasaRandomPictures =
            "https://api.nasa.gov/planetary/apod?api_key={api_key}&count={size}";
    private String nasaLatestPictures =
            "https://api.nasa.gov/planetary/apod?api_key={api_key}&start_date={start_date}&end_date={end_date}";
    private static final String NASA_API_KEY =
            "DEMO_KEY";
    private static final long DAY_CONSTANT = 86400000;


    private static final String GALLERY_ARRAY_KEY = "gallery_array";

    private ArrayList<ItemModel> savedArray;

    private String chosenLoadingPreference = "";
    private String chosenGallerySize;
    private String asyncTaskFinalUrl = "";
    private CustomRecyclerViewAdapter adapter;

    @BindView(R.id.latest_pictures)
    RecyclerView pictureGallery;
    @BindView(R.id.spin_kit)
    SpinKitView primaryLoader;


    public GalleryFragment() {
    }


    public class ReceiveNasaDataTaskCompleteListener implements AsyncTaskCompleteListener<ArrayList<ItemModel>> {

        @Override
        public void onTaskComplete(ArrayList<ItemModel> result) {
            if (result == null) {
                return;
            }

            savedArray = result;
            primaryLoader.setVisibility(View.GONE);
            adapter.setGalleryData(result);
            pictureGallery.setAdapter(adapter);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.gallery_fragment, container, false);
        ButterKnife.bind(this, rootView);

        readFromSharedPreferences();

        adapter = new CustomRecyclerViewAdapter(null, getContext());

        pictureGallery.setLayoutManager(new GridLayoutManager(getContext(), 3));

        pictureGallery.setAdapter(adapter);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(GALLERY_ARRAY_KEY) != null) {
            savedArray = savedInstanceState.getParcelableArrayList(GALLERY_ARRAY_KEY);
            CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter(savedArray, getContext());
            pictureGallery.setAdapter(adapter);
        } else {

            executeAsyncTask();
        }

        return rootView;
    }

    private void readFromSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        chosenLoadingPreference = sharedPreferences.getString(getString(R.string.pref_loading_key),
                getString(R.string.random_pictures_value));
        chosenGallerySize = sharedPreferences.getString(getString(R.string.pref_size_key),
                getString(R.string.pref_size_default_value));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void executeAsyncTask() {

        if (chosenLoadingPreference.equals(getString(R.string.random_pictures_value))) {

            primaryLoader.setVisibility(View.VISIBLE);

            asyncTaskFinalUrl = nasaRandomPictures
                    .replace("{api_key}", NASA_API_KEY)
                    .replace("{size}", chosenGallerySize);

            new ReceiveGalleryPicturesTask(getContext(), new ReceiveNasaDataTaskCompleteListener()).execute(asyncTaskFinalUrl);


        } else if (chosenLoadingPreference.equals(getString(R.string.latest_pictures_label))) {

            primaryLoader.setVisibility(View.VISIBLE);

            int numberOfGalleryItems = Integer.parseInt(chosenGallerySize);

            long currentDate = System.currentTimeMillis();
            long customPastDate = currentDate - (DAY_CONSTANT * (numberOfGalleryItems - 1));
            String convertedCurrentDate = dateToStringConverter(currentDate);
            String convertedPastDate = dateToStringConverter(customPastDate);

            asyncTaskFinalUrl = nasaLatestPictures
                    .replace("{api_key}", NASA_API_KEY)
                    .replace("{start_date}", convertedPastDate)
                    .replace("{end_date}", convertedCurrentDate);

            new ReceiveGalleryPicturesTask(getContext(), new ReceiveNasaDataTaskCompleteListener()).execute(asyncTaskFinalUrl);

        }
    }

    private String dateToStringConverter(long dateInMillis) {
        String convertedDate;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        convertedDate = formatter.format(dateInMillis);
        return convertedDate;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_loading_key))) {
            chosenLoadingPreference = sharedPreferences.getString(getString(R.string.pref_loading_key),
                    getString(R.string.random_pictures_value));
        }
        if (key.equals(getString(R.string.pref_size_key))) {
            chosenGallerySize = sharedPreferences.getString(getString(R.string.pref_size_key),
                    getString(R.string.pref_size_default_value));
        }
        adapter.setGalleryData(null);
        executeAsyncTask();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(GALLERY_ARRAY_KEY, savedArray);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
