package com.briatka.pavol.closertostars.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.briatka.pavol.closertostars.CustomRecyclerViewAdapter;
import com.briatka.pavol.closertostars.ItemModel;
import com.briatka.pavol.closertostars.R;
import com.briatka.pavol.closertostars.asyncTasks.ReceiveGalleryPicturesTask;
import com.briatka.pavol.closertostars.interfaces.AsyncTaskCompleteListener;
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

    private static final String TASK_IS_RUNNING_KEY = "gallery_task_state";


    private static final String GALLERY_ARRAY_KEY = "gallery_array";

    private ArrayList<ItemModel> savedArray;

    private String chosenLoadingPreference = "";
    private String chosenGallerySize;
    private String asyncTaskFinalUrl = "";
    private CustomRecyclerViewAdapter adapter;

    private ReceiveGalleryPicturesTask receiveGalleryPicturesTask;
    private boolean myAsyncTaskIsRunning = true;

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

            myAsyncTaskIsRunning = false;
            receiveGalleryPicturesTask = null;

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

        if (savedInstanceState != null) {
            myAsyncTaskIsRunning = savedInstanceState.getBoolean(TASK_IS_RUNNING_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(GALLERY_ARRAY_KEY) != null) {
            savedArray = savedInstanceState.getParcelableArrayList(GALLERY_ARRAY_KEY);
            CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter(savedArray, getContext());
            pictureGallery.setAdapter(adapter);
        } else {
            if (myAsyncTaskIsRunning) {
                executeAsyncTask();
            }
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

            receiveGalleryPicturesTask = new ReceiveGalleryPicturesTask(getContext(), new ReceiveNasaDataTaskCompleteListener());
            receiveGalleryPicturesTask.execute(asyncTaskFinalUrl);


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

            receiveGalleryPicturesTask = new ReceiveGalleryPicturesTask(getContext(), new ReceiveNasaDataTaskCompleteListener());
            receiveGalleryPicturesTask.execute(asyncTaskFinalUrl);
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
        if(checkConnection()){
        executeAsyncTask();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.no_connection_msg_preferences),
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(GALLERY_ARRAY_KEY, savedArray);
        outState.putBoolean(TASK_IS_RUNNING_KEY, myAsyncTaskIsRunning);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        if (receiveGalleryPicturesTask != null) receiveGalleryPicturesTask.cancel(true);
        receiveGalleryPicturesTask = null;
    }
}
