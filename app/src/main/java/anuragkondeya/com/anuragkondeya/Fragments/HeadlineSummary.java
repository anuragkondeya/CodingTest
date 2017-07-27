package anuragkondeya.com.anuragkondeya.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import anuragkondeya.com.anuragkondeya.Constants;
import anuragkondeya.com.anuragkondeya.Data.StoriesLoader;
import anuragkondeya.com.anuragkondeya.Data.Story;
import anuragkondeya.com.anuragkondeya.R;


public class HeadlineSummary extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Story>>, LoadDataNotifier {

    /**
     * Loader id
     */
    private final int LOADER_ID = 0;

    private final String KEY_LAST_POSITION = "key_last_position";

    /**
     * Instance of the listview
     */
    ListView mHeadLineListView = null;

    /**
     * List to store story instances which contains data fetched from the server
     */
    static List<Story> mStoryList = null;

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
     * Progress bar will be displayed when fetching data after user reaches last in the list
     */
    ProgressBar mProgressBar = null;

    /**
     * offset for pagination
     */
    private int mOffset = 0;

    /**
     * Splash screen view
     */
    private View mSplash;

    /**
     * Current scroll position
     */
    static int mCurrentPosition = 0;


    /**
     * Story item click listner called when a story item is clicked by the user to display the complete story
     *
     * @return
     */
    private AdapterView.OnItemClickListener storyItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
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
                        .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                        .replace(R.id.headlines_frame_container, storyViewInstance)
                        .addToBackStack(null)
                        .commit();


            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_headline_summary, container, false);
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
            if (null == networkInfo)
                return false;
            else
                return true;
        }
    }


    /**
     * User it stuck in case of no network. Hence proving a way out.
     * @return
     */
    private View.OnClickListener closeButonOnClickListener(){
        return new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        mHeadLineListView = (ListView) view.findViewById(R.id.headlineListView);
        mSplash = view.findViewById(R.id.splash);
        if(!isNetworkConnected()){
            //Button to close app, (can implement better solution)
            Button button = (Button)view.findViewById(R.id.closeApp);
            button.setVisibility(View.VISIBLE);
            button.setFocusable(true);
            button.setOnClickListener(closeButonOnClickListener());
            Toast.makeText(getActivity(), R.string.network_toast, Toast.LENGTH_SHORT).show();

        }
        if (null == mStoryList) {
            mStoryList = new ArrayList<>();
            mSplash.setVisibility(View.VISIBLE);
        }
        if(mStoryList.size()>0)
            mSplash.setVisibility(View.GONE);
        mLoaderCallback = this;
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mAdapter = new HeadLineListAdaper(getContext(), mStoryList);
        mHeadLineListView.setAdapter(mAdapter);
        mHeadLineListView.setSelection(mCurrentPosition);

        mHeadLineListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mCurrentPosition = firstVisibleItem;
            }
        });
        mHeadLineListView.setOnItemClickListener(storyItemClickListener());
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
               if(mSplash.isShown())
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
    public void notifyLoadData() {
        mOffset += 10;
        getLoaderManager().restartLoader(LOADER_ID, null, mLoaderCallback);
        if (null == mProgressBar) {
            mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        }
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}