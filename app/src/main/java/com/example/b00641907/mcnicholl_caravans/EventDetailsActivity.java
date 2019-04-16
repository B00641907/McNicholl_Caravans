package com.example.b00641907.mcnicholl_caravans;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.b00641907.mcnicholl_caravans.model.EventInfo;
import com.example.b00641907.mcnicholl_caravans.view.PageNavigator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class EventDetailsActivity extends BaseActivity {

    // Event Information
    private EventInfo eventInfo;

    // Image Loader
    ImageLoader mImageLoader;
    DisplayImageOptions mImageOptions;

    ImageView ivEvent;
    TextView tvEventName;
    TextView tvEventTime;
    TextView tvEventDescripton;

    // Image Fragment
    private FragmentStatePagerAdapter adapter;
    private ViewPager viewPager;
    PageNavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventInfo = getIntent().getParcelableExtra("event_info");
        if (eventInfo == null) {
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(eventInfo.getName());
        }

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

        // Show Titles and Description
        ivEvent = findViewById(R.id.ivEvent);
        tvEventName = findViewById(R.id.tvName);
        tvEventTime = findViewById(R.id.tvTime);
        tvEventDescripton = findViewById(R.id.tvDescrpition);

        mImageLoader.displayImage(eventInfo.getImage(), ivEvent, mImageOptions);
        tvEventName.setText(eventInfo.getName());
        tvEventTime.setText(eventInfo.getTime());
        tvEventDescripton.setText(eventInfo.getDescription());
    }
}
