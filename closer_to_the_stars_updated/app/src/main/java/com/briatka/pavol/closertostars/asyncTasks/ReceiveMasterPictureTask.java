package com.briatka.pavol.closertostars.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.briatka.pavol.closertostars.ItemModel;
import com.briatka.pavol.closertostars.interfaces.MasterPictureAsyncTaskCompleteListener;

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

public class ReceiveMasterPictureTask extends AsyncTask<String, Void, ItemModel> {

    private Context mContext;
    private MasterPictureAsyncTaskCompleteListener<ItemModel> mListener;
    private static final String NASA_URL =
            "https://api.nasa.gov/planetary/apod?api_key=D68zDm7H0OwHgnWX5rvoHXsdVktVnyFvN0p0nFAJ";


    public ReceiveMasterPictureTask(Context context,
                                    MasterPictureAsyncTaskCompleteListener<ItemModel> listener) {
        this.mContext = context;
        this.mListener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ItemModel doInBackground(String... strings) {
        URL url;

        url = createUrl(NASA_URL);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nasaApod(jsonResponse);
    }

    @Override
    protected void onPostExecute(ItemModel pictureOfDay) {
        super.onPostExecute(pictureOfDay);
        mListener.onPictureReceived(pictureOfDay);


    }


    private URL createUrl(String urlString) {
        URL url;
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

    private ItemModel nasaApod(String jsonString) {

        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            JSONObject baseResponse = new JSONObject(jsonString);
            String imgSrc = baseResponse.getString("url");
            String description = baseResponse.getString("explanation");
            String title = baseResponse.getString("title");
            String mediaType = baseResponse.getString("media_type");
            String date = baseResponse.getString("date");


            return new ItemModel(imgSrc, description, title, mediaType, date);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}


