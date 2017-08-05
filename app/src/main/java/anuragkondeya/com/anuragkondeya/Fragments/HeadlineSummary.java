package anuragkondeya.com.anuragkondeya.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.transition.TransitionInflater;
import android.util.Log;
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
import anuragkondeya.com.anuragkondeya.Data.Client;
import anuragkondeya.com.anuragkondeya.Data.Story;
import anuragkondeya.com.anuragkondeya.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HeadlineSummary extends Fragment implements LoadDataNotifier {

    /**
     * List to store story instances which contains data fetched from the server
     */
    static List<Story> mStoryList = null;
    /**
     * Current scroll position
     */
    static int mCurrentPosition = 0;

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
     * Story item click listner called when a story item is clicked by the user to display the complete story
     *
     * @return
     */

    String BASEURL = "https://aboutdoor.info/";
    /**
     * BUtterknife unbinder instance
     */
    private Unbinder unbinder;
    /**
     * offset for pagination
     */
    private int mOffset = 0;

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

        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(thumbnail, ViewCompat.getTransitionName(thumbnail))
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


    public void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASEURL)
                .build();

        Client client = retrofit.create(Client.class);
        Observable<List<Story>> newsItemObservable = client.getNewsData(mOffset);
        newsItemObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Story>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Anurag", "Error " + e);
                    }

                    @Override
                    public void onNext(List<Story> newsItems) {
                        if (mSplash.isShown())
                            mSplash.setVisibility(View.GONE);
                        mStoryList.addAll(newsItems);
                        mAdapter.notifyDataSetChanged();
                        if (null != mProgressBar)
                            mProgressBar.setVisibility(View.INVISIBLE);

                    }
                });

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
        mProgressBar.setVisibility(View.INVISIBLE);
        mAdapter = new HeadLineListAdaper(getContext(), mStoryList);
        mHeadLineListView.setAdapter(mAdapter);
        mHeadLineListView.setSelection(mCurrentPosition);
        mHeadLineListView.setOnScrollListener(onScrollListener());
        mAdapter.setLoadDataNotifierLstener(this);
        getData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void notifyLoadData() {
        if (null != mProgressBar)
            mProgressBar.setVisibility(View.VISIBLE);
        mOffset += 10;
        getData();
    }
}
