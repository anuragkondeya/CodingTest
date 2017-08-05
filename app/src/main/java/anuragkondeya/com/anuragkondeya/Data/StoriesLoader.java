package anuragkondeya.com.anuragkondeya.Data;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.Loader;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

import anuragkondeya.com.anuragkondeya.Constants;


/**
 * Custom loader implementation
 */
public class StoriesLoader extends Loader<List<Story>> {

    private static final String BASE_URL = "https://aboutdoor.info/news?index=";
    private final String TAG_VOLLEY = "volleyTag";
    private List<Story> mCachedStories = null;
    private Context mAppContext;
    private int mOffset = 0;

    private Gson gson;

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
            return null != networkInfo;
        }
    }


    /**
     * parse JSON and fill the stories list with story object
     * @param answer
     */
    private void processResult(String answer) {
        if (null != answer) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
            mCachedStories = Arrays.asList(gson.fromJson(answer, Story[].class));
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

    @Override
    protected void onForceLoad() {
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
