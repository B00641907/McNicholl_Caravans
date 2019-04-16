package com.example.b00641907.mcnicholl_caravans.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.util.List;

public class CaravansImageAdapter extends BaseAdapter {

    public interface OnCaravanImageActionListener {
        void onItemImageEdit(int position);
        void onItemImageRemove(int position);
    }

    private Context mContext;
    private List<String> mDataList;
    private OnCaravanImageActionListener mImageActionListener;

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mImageOptions;

    public CaravansImageAdapter(Context mContext, List<String> dataList, OnCaravanImageActionListener imageActionListener) {
        this.mContext = mContext;
        this.mDataList = dataList;
        this.mImageActionListener = imageActionListener;

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
    }

    @Override
    public int getCount() {
        if (mDataList == null)
            return 0;
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_caravans_image,
                    null);
        }

        // Gas Image
        final ImageView ivCaravan = (ImageView) convertView.findViewById(R.id.ivCaravan);

        // Show Image
        String imagePath = (String) getItem(position);
        if (imagePath.startsWith("http")) {
            mImageLoader.displayImage(imagePath, ivCaravan, mImageOptions);
        } else {
            String decodedImgUri = Uri.fromFile(new File(imagePath)).toString();
            mImageLoader.displayImage(decodedImgUri, ivCaravan, mImageOptions);
        }

        // Action Buttons
        View btnEdit = convertView.findViewById(R.id.btnEdit);
        btnEdit.setTag(position);
        btnEdit.setOnClickListener(editBtnClickListener);

        View btnRemove = convertView.findViewById(R.id.btnRemove);
        btnRemove.setTag(position);
        btnRemove.setOnClickListener(removeBtnClickListener);

        return convertView;
    }

    View.OnClickListener editBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            if (mImageActionListener != null) {
                mImageActionListener.onItemImageEdit(position);
            }
        }
    };

    View.OnClickListener removeBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();

            if (mImageActionListener != null) {
                mImageActionListener.onItemImageRemove(position);
            }
        }
    };
}
