package anuragkondeya.com.anuragkondeya.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import anuragkondeya.com.anuragkondeya.Data.Story;
import anuragkondeya.com.anuragkondeya.R;

/**
 * Created by anuragkondeya on 26/7/17.
 */

public class HeadLineListAdaper extends ArrayAdapter<Story> {


    /**
     * List storing story items fetched from the server
     */
    private List<Story> mStories;

    /**
     * Volley request queue instance
     */
    private RequestQueue mRequestQueue;

    /**
     * Data notifier notifier loader to fetch data when user reaches last of the list
     */
    private LoadDataNotifier mLoadDataNotifier = null;

    /**
     * View holder for recycling views of list items
     */
    private class ViewHolder {
        TextView headlineTextView;
        ImageView thumbnailImageView;
    }


    /**
     * List view adapter instance
     * @param context
     * @param stories
     */
    public HeadLineListAdaper(Context context, List<Story> stories) {
        super(context, R.layout.headline_cell_view, stories);
        mStories = stories;
    }

    /**
     * Set data notfier callback
     * @param loadDataNotifier
     */
    public void setLoadDataNotifierLstener(LoadDataNotifier loadDataNotifier) {
        mLoadDataNotifier = loadDataNotifier;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Story story = mStories.get(position);
        int lastPosition = mStories.size() - 1;
        if (position == lastPosition) {
            if (null != mLoadDataNotifier)
                mLoadDataNotifier.notifyLoadData();
        }
        ViewHolder viewHolder = null;
        View view = null;
        /**
         * Recycling views here as find view by id calls are costly
         */
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.headline_cell_view, parent, false);
            viewHolder.headlineTextView = (TextView) convertView.findViewById(R.id.headlineTextViewInList);
            viewHolder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.thumbnail);
            view = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            view = convertView;
        }
        viewHolder.headlineTextView.setText(story.headline);
        final ImageView thumbnailView = viewHolder.thumbnailImageView;
        thumbnailView.setImageDrawable(null);
        final BitmapCache bitmapCache = BitmapCache.getInstance();
        final Bitmap bitmap = bitmapCache.getBitmapFromMemCache(story.id);
        if (null != bitmap) {
            thumbnailView.setImageBitmap(bitmap);
        } else {

            // Can use glide here for better user experience
            ImageRequest imageRequest = new ImageRequest(
                    story.imageURL,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            bitmapCache.addBitmapToMemoryCache(story.id, response);
                            thumbnailView.setImageBitmap(response);
                        }
                    },
                    0,
                    0,
                    ImageView.ScaleType.FIT_XY,
                    Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something with error response
                            error.printStackTrace();
                        }
                    }
            );
            mRequestQueue = Volley.newRequestQueue(getContext());
            mRequestQueue.add(imageRequest);
        }
        return view;
    }
}
