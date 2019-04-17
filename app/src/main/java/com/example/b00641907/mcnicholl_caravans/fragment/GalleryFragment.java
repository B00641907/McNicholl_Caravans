package com.example.b00641907.mcnicholl_caravans.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.b00641907.mcnicholl_caravans.R;
import com.example.b00641907.mcnicholl_caravans.CustomerCaravanViewActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GalleryFragment extends Fragment {

    private ImageLoader imageLoader;
    private DisplayImageOptions imageOptions;

    private String imageResource;
    public static GalleryFragment getInstance(String imageSource) {
        GalleryFragment f = new GalleryFragment();
        Bundle args = new Bundle();

        args.putString("image_source", imageSource);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageResource = getArguments().getString("image_source");

        imageLoader = ((CustomerCaravanViewActivity) getActivity()).getImageLoader();
        imageOptions = ((CustomerCaravanViewActivity) getActivity()).getImageOptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_imagegallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set Image
        final ImageView imageView = (ImageView) view.findViewById(R.id.image);


        imageLoader.displayImage(imageResource, imageView, imageOptions);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
