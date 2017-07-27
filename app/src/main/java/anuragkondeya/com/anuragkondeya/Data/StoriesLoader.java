package anuragkondeya.com.anuragkondeya.Data;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.Loader;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import anuragkondeya.com.anuragkondeya.Constants;


/**
 * Custom loader implementation
 */
public class StoriesLoader extends Loader<List<Story>> {

    private final String TAG_VOLLEY = "volleyTag";
    private List<Story> mCachedStories = null;
    private static final String BASE_URL = "https://aboutdoor.info/news?index=";
    private Context mAppContext;
    private int mOffset = 0;

    private final String FIELD_ABSTRACT = "absract";
    private final String FIELD_BODY = "body";
    private final String FIELD_HEADLINE = "headline";
    private final String FIELD_ID = "id";
    private final String FIELD_IMAGE = "image";

    private RequestQueue mRequestQueue;


    public StoriesLoader(Context context, int offset) {
        super(context);
        mAppContext = context;
        mOffset = offset;
    }


    /**
     * Terminate all network request when listview is no longer visible
     */
    public void onStop() {
        if (null != mRequestQueue) {
            mRequestQueue.cancelAll(TAG_VOLLEY);
        }
    }

    /**
     * Get status of the network connection
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mAppContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager)
            return false;
        else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (null == networkInfo)
                return false;
            else
                return true;
        }
    }


    /**
     * parse JSON and fill the stories list with story object
     * @param answer
     */
    private void processResult(String answer) {
        if (null != answer) {
            try {
                JSONArray jsonArray = new JSONArray(answer);
                mCachedStories = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Story story = new Story();
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    //story.abstractText = jsonObject.getString(FIELD_ABSTRACT);
                    story.body = jsonObject.getString(FIELD_BODY);
                    story.headline = jsonObject.getString(FIELD_HEADLINE);
                    story.id = jsonObject.getString(FIELD_ID);
                    story.imageURL = jsonObject.getString(FIELD_IMAGE);
                    mCachedStories.add(story);
                    story = null;
                }
            } catch (JSONException e) {
                //TODO throw error here
                e.printStackTrace();
            }
            deliverResult(mCachedStories);
        }
    }

    /**
     * Log network errors
     * @param Error
     */
    private void processError(String Error) {
        Log.i(Constants.TAG,"Network error "+Error);
    }


    /**
     * Execute network calls using volley
     */
    private void executeNetworkCallsUsingVolley() {
        mRequestQueue = Volley.newRequestQueue(mAppContext);
        String url = BASE_URL + mOffset;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setTag(TAG_VOLLEY);
        mRequestQueue.add(stringRequest);
    }


    /**
     *
     * Execute network calls using AsyncTask
     * Just in case using volley is not allowed
     */
    private void executeNetworkCallsUsingAsyncTask() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isNetworkConnected()) {
                    URL url = null;
                    try {
                        url = new URL(BASE_URL + mOffset);
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        connection.connect();
                        BufferedReader reader = null;
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String answer = reader.readLine();
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            processResult(answer);
                        } else {
                            processError(answer);
                        }
                        connection.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }


    @Override
    protected void onForceLoad() {
         //executeNetworkCallsUsingAsyncTask();
        executeNetworkCallsUsingVolley();
    }

    @Override
    protected void onStartLoading() {
        if (null == mCachedStories) {
            forceLoad();
        } else {
            super.deliverResult(mCachedStories);
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected void onReset() {
        super.onReset();
        Volley.newRequestQueue(mAppContext).cancelAll(TAG_VOLLEY);
    }

    @Override
    public void deliverResult(List<Story> stories) {
        mCachedStories = stories;
        if (isStarted()) {
            super.deliverResult(stories);
        }
    }
}
