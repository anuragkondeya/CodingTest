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

/**
 * Fragments to display individual story
 */
public class StoryView extends Fragment {

    private Story mStory = null;

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
            View view = getView();
            ImageView image = (ImageView) view.findViewById(R.id.storyImage);
            CollapsingToolbarLayout headline = (CollapsingToolbarLayout) view.findViewById(R.id.headlineStoryView);
            TextView body = (TextView) view.findViewById(R.id.storyBody);
            BitmapCache instance = BitmapCache.getInstance();
            image.setImageBitmap(instance.getBitmapFromMemCache(story.id));
            headline.setTitle(story.headline);
            body.setText(story.body);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateStoryItem(mStory);
    }
}
