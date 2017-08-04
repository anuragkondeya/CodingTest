package anuragkondeya.com.anuragkondeya.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import anuragkondeya.com.anuragkondeya.Constants;
import anuragkondeya.com.anuragkondeya.Data.StoriesLoader;
import anuragkondeya.com.anuragkondeya.Data.Story;
import anuragkondeya.com.anuragkondeya.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;


public class HeadlineSummary extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Story>>, LoadDataNotifier {

    /**
     * List to store story instances which contains data fetched from the server
     */
    static List<Story> mStoryList = null;
    /**
     * Current scroll position
     */
    static int mCurrentPosition = 0;
    /**
     * Loader id
     */
    private final int LOADER_ID = 0;
    private final String KEY_LAST_POSITION = "key_last_position";
    /**
     * Instance of the listview
     */
    @BindView(R.id.headlineListView)
    ListView mHeadLineListView;
    /**
     * close app button if network is not present
     */
    @BindView(R.id.closeApp)
    Button closeApp;
    @BindView(R.id.splash)
    View mSplash;
    /**
     * Progress bar will be displayed when fetching data after user reaches last in the list
     */
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    /**
     * Listview adapter
     */
    HeadLineListAdaper mAdapter = null;

    /**
     * loader instance
     */
    StoriesLoader mStoriesLoader = null;

    /**
     * loader callback instance
     */
    LoaderManager.LoaderCallbacks<List<Story>> mLoaderCallback;
    /**
     * BUtterknife unbinder instance
     */
    private Unbinder unbinder;
    /**
     * offset for pagination
     */
    private int mOffset = 0;

    /**
     * Story item click listner called when a story item is clicked by the user to display the complete story
     *
     * @return
     */

    @OnItemClick(R.id.headlineListView)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //final ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
        Story story = mStoryList.get(position);
        StoryView storyViewInstance = new StoryView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            storyViewInstance.setSharedElementEnterTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(R.transition.transition));

            storyViewInstance.setEnterTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(android.R.transition.fade));

            storyViewInstance.setExitTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(android.R.transition.fade));
        }

        Bundle extras = new Bundle();
        extras.putParcelable(Constants.KEY_STORY_OBEJCT, story);
        storyViewInstance.setArguments(extras);
        getFragmentManager()
                .beginTransaction()
                //.addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                .replace(R.id.headlines_frame_container, storyViewInstance)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_headline_summary, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LAST_POSITION, mCurrentPosition);
    }


    /**
     * Get status of the network connection
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager)
            return false;
        else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return null != networkInfo;
        }
    }

    /**
     * User it stuck in case of no network. Hence proving a way out.
     *
     * @return
     */
    @OnClick(R.id.closeApp)
    public void onClick(View v) {
        getActivity().finish();
    }


    private AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mCurrentPosition = firstVisibleItem;
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkConnected()) {
            //Button to close app, (can implement better solution)
            closeApp.setVisibility(View.VISIBLE);
            closeApp.setFocusable(true);
            Toast.makeText(getActivity(), R.string.network_toast, Toast.LENGTH_SHORT).show();
        }
        if (null == mStoryList) {
            mStoryList = new ArrayList<>();
            mSplash.setVisibility(View.VISIBLE);
        }
        if (mStoryList.size() > 0)
            mSplash.setVisibility(View.GONE);
        mLoaderCallback = this;
        mProgressBar.setVisibility(View.INVISIBLE);
        mAdapter = new HeadLineListAdaper(getContext(), mStoryList);
        mHeadLineListView.setAdapter(mAdapter);
        mHeadLineListView.setSelection(mCurrentPosition);

        mHeadLineListView.setOnScrollListener(onScrollListener());
        // mHeadLineListView.setOnItemClickListener(storyItemClickListener());
        mAdapter.setLoadDataNotifierLstener(this);
        getLoaderManager().initLoader(LOADER_ID, null, mLoaderCallback);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != mStoriesLoader)
            mStoriesLoader.onStop();
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        mStoriesLoader = new StoriesLoader(getActivity(), mOffset);
        return mStoriesLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, final List<Story> data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSplash.isShown())
                    mSplash.setVisibility(View.GONE);
                mStoryList.addAll(data);
                mAdapter.notifyDataSetChanged();
                if (null != mProgressBar)
                    mProgressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void notifyLoadData() {
        mOffset += 10;
        getLoaderManager().restartLoader(LOADER_ID, null, mLoaderCallback);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
