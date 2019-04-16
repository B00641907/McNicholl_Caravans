package com.example.b00641907.mcnicholl_caravans;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import com.example.b00641907.mcnicholl_caravans.adapter.GalleryPageAdapter;
import com.example.b00641907.mcnicholl_caravans.model.CaravanInfo;
import com.example.b00641907.mcnicholl_caravans.view.PageNavigator;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;




import java.util.List;

public class CustomerCaravanViewActivity extends BaseActivity implements View.OnClickListener {

    // Caravan Information
    private CaravanInfo caravanInfo;

    // Firebase Authentication
    private FirebaseAuth firebaseAuth;

    // Image Loader
    ImageLoader mImageLoader;
    DisplayImageOptions mImageOptions;

    TextView tvCaravanTitle;
    TextView tvCaravanDetails;

    // Image Fragment
    private FragmentStatePagerAdapter adapter;
    private ViewPager viewPager;
    PageNavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_caravanview);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        caravanInfo = getIntent().getParcelableExtra("caravan_info");
        if (caravanInfo == null) {
            finish();
            return;
        }

        // Show Titles and Description
        tvCaravanTitle = findViewById(R.id.tvCaravanTitle);
        tvCaravanDetails = findViewById(R.id.tvCaravanDetails);

        tvCaravanTitle.setText(caravanInfo.getName());
        tvCaravanDetails.setText(caravanInfo.getDescription());

        // Add Avatar Image to Image List
        List<String> images = caravanInfo.getImages();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        navigator = (PageNavigator) findViewById(R.id.navigator);
        navigator.setSize(caravanInfo.getImages().size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                navigator.setPosition(position);
                navigator.invalidate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext.getApplicationContext()));
        mImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_placeholder)
                .showImageOnFail(R.drawable.ic_placeholder)
                .showImageForEmptyUri(R.drawable.ic_placeholder)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        // init viewpager adapter and attach
        adapter = new GalleryPageAdapter(getSupportFragmentManager(), images, mImageLoader, mImageOptions);
        viewPager.setAdapter(adapter);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public DisplayImageOptions getImageOptions() {
        return mImageOptions;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnBack) {
            finish();
        }
    }
}
