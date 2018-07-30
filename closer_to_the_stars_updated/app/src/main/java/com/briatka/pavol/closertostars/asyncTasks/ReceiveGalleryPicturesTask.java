package com.briatka.pavol.closertostars.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.briatka.pavol.closertostars.ItemModel;
import com.briatka.pavol.closertostars.interfaces.AsyncTaskCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ReceiveGalleryPicturesTask extends AsyncTask<String, Void, ArrayList<ItemModel>> {

    private Context mContext;
    private AsyncTaskCompleteListener<ArrayList<ItemModel>> mListener;

    public ReceiveGalleryPicturesTask(Context context, AsyncTaskCompleteListener<ArrayList<ItemModel>> listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<ItemModel> doInBackground(String... strings) {
        URL url;

        url = createUrl(strings[0]);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayOfPictures(jsonResponse);
    }

    @Override
    protected void onPostExecute(ArrayList<ItemModel> pictureOfDay) {
        super.onPostExecute(pictureOfDay);
        mListener.onTaskComplete(pictureOfDay);
    }


    private URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(15000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private ArrayList<ItemModel> arrayOfPictures(String jsonString) {

        ArrayList<ItemModel> arrayOfPictures = new ArrayList<>();

        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            JSONArray baseResponse = new JSONArray(jsonString);
            for (int i = baseResponse.length() - 1; i >= 0; i--) {
                JSONObject singleObject = baseResponse.getJSONObject(i);
                String imgSrc = singleObject.optString("url");
                String description = singleObject.optString("explanation");
                String title = singleObject.optString("title");
                String mediaType = singleObject.optString("media_type");
                String date = singleObject.optString("date");


                arrayOfPictures.add(new ItemModel(imgSrc, description, title, mediaType, date));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayOfPictures;
    }
}

