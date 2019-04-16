package com.example.b00641907.mcnicholl_caravans.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.example.b00641907.mcnicholl_caravans.fragment.GalleryFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.List;

public class GalleryPageAdapter extends FragmentStatePagerAdapter {

    private List<String> images;
    ImageLoader imageLoader;
    DisplayImageOptions imageOptions;

    public GalleryPageAdapter(FragmentManager fm, List<String> images, ImageLoader imageLoader, DisplayImageOptions imageOPtions) {
        super(fm);
        this.images = images;
        this.imageLoader = imageLoader;
        this.imageOptions = imageOptions;
    }

    @Override
    public Fragment getItem(int position) {
        return GalleryFragment.getInstance(images.get(position));
    }

    @Override
    public int getCount() {
        if (images == null) {
            return 0;
        } else {
            return images.size();
        }
    }
}
