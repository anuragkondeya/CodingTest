package anuragkondeya.com.anuragkondeya.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import anuragkondeya.com.anuragkondeya.Constants;
import anuragkondeya.com.anuragkondeya.Data.Story;
import anuragkondeya.com.anuragkondeya.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragments to display individual story
 */
public class StoryView extends Fragment {

    @BindView(R.id.storyImage)
    ImageView image;
    @BindView(R.id.storyBody)
    TextView body;
    @BindView(R.id.headlineStoryView)
    CollapsingToolbarLayout headline;
    private Story mStory = null;
    /**
     * BUtterknife unbinder instance
     */
    private Unbinder unbinder;
    public StoryView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext())
                    .inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        if (null != arguments) {
            mStory = arguments.getParcelable(Constants.KEY_STORY_OBEJCT);
        }
    }


    /**
     * update a story item
     *
     * @param story
     */
    public void updateStoryItem(Story story) {
        if (null != story) {
            BitmapCache instance = BitmapCache.getInstance();
            image.setImageBitmap(instance.getBitmapFromMemCache(story.id));
            headline.setTitle(story.headline);
            body.setText(story.body);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateStoryItem(mStory);
    }
}
