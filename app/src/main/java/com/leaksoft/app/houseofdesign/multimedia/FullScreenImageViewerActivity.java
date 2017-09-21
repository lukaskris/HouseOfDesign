package com.leaksoft.app.houseofdesign.multimedia;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.leaksoft.app.houseofdesign.R;

import java.util.ArrayList;
import java.util.List;

public class FullScreenImageViewerActivity extends AppCompatActivity {
    ViewPager viewPager;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_viewer);
        viewPager = (ViewPager) findViewById(R.id.full_screen_image_viewer_viewpager);
        List<String> url=new ArrayList<>();

        url = (List<String>) getIntent().getSerializableExtra("images");
        int posisi = getIntent().getIntExtra("posisi",0);

        FullScreenPagerAdapter adapter = new FullScreenPagerAdapter(this,url);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(posisi);
        findViewById(R.id.image_viewer_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class FullScreenPagerAdapter extends PagerAdapter{
        private List<String> url;
        Context mContext;
        LayoutInflater mLayoutInflater;

        FullScreenPagerAdapter(Context context,List<String>url){
            this.url = url;mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return url.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((FrameLayout)object);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.layout_image_viewer, container, false);
            GestureImageView image = (GestureImageView) itemView.findViewById(R.id.image_viewer_image);
            image.getController().getSettings()
                    .setMaxZoom(2f)
                    .setDoubleTapZoom(-1f) // Falls back to max zoom level
                    .setPanEnabled(true)
                    .setZoomEnabled(true)
                    .setDoubleTapEnabled(true)
                    .setRotationEnabled(false)
                    .setRestrictRotation(false)
                    .setOverscrollDistance(0f, 0f)
                    .setOverzoomFactor(2f)
                    .setFillViewport(true)
                    .setFitMethod(Settings.Fit.INSIDE)
                    .setGravity(Gravity.CENTER);

            final ProgressBar mLoading = (ProgressBar) itemView.findViewById(R.id.image_viewer_loading);
            Glide.with(mContext).load(url.get(position)).diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    mLoading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    mLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(image);


            container.addView(itemView);

            return itemView;
        }
    }
}
