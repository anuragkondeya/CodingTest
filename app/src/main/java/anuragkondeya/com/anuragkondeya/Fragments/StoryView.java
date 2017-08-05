package anuragkondeya.com.anuragkondeya.Fragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

import anuragkondeya.com.anuragkondeya.Constants;
import anuragkondeya.com.anuragkondeya.Data.Story;
import anuragkondeya.com.anuragkondeya.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Fragments to display individual story
 */
public class StoryView extends Fragment {

    @BindView(R.id.storyImage)
    ImageView storyImage;
    @BindView(R.id.storyBody)
    TextView body;
    @BindView(R.id.storyHeadline)
    TextView headline;

    @BindView(R.id.backButton)
    ImageButton backButton;
    Fragment storyFragment;
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
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        unbinder = ButterKnife.bind(this, view);
        storyFragment = this;
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
     * User it stuck in case of no network. Hence proving a way out.
     *
     * @return
     */
    @OnClick(R.id.backButton)
    public void onClick(View v) {
        getFragmentManager().popBackStack();
    }

    /**
     * update a story item
     *
     * @param story
     */
    public void updateStoryItem(Story story) {
        if (null != story) {
            //Will get storyImage cached by glide
            Glide.with(getContext())
                    .load(story.image)
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            startPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new ImageViewTarget<Bitmap>(storyImage) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            if (null != resource) {
                                storyImage.setImageBitmap(resource);
                                setColors(resource);
                            }
                        }
                    });
            headline.setText(story.headline);
            body.setText(story.body);
        }
    }

    private void setColors(Bitmap bitmap) {
        new Palette.Builder(bitmap)
                .setRegion(0, 0, 50, 50)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        if (null != swatch) {
                            backButton.setColorFilter(swatch.getTitleTextColor());
                        }
                    }
                });
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
