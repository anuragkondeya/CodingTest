package anuragkondeya.com.anuragkondeya.Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import anuragkondeya.com.anuragkondeya.Data.Story;
import anuragkondeya.com.anuragkondeya.R;
import butterknife.BindView;
import butterknife.ButterKnife;

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

        /**
         * Recycling views here as find view by id calls are costly
         */
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.headline_cell_view, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.headlineTextView.setText(story.headline);
        final ImageView thumbnailView = viewHolder.thumbnailImageView;
        thumbnailView.setImageDrawable(null);

        Glide.with(getContext())
                .load(story.image)
                .override(150, 150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbnailView);
        return convertView;
    }

    /**
     * View holder for recycling views of list items
     */
    static class ViewHolder {
        @BindView(R.id.headlineTextViewInList)
        TextView headlineTextView;
        @BindView(R.id.thumbnail)
        ImageView thumbnailImageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
