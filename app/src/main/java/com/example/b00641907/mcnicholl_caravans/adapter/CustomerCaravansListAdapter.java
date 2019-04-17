package com.example.b00641907.mcnicholl_caravans.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.b00641907.mcnicholl_caravans.R;
import com.example.b00641907.mcnicholl_caravans.model.CaravanInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class CustomerCaravansListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CaravanInfo> mDataList;

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mImageOptions;

    public CustomerCaravansListAdapter(Context mContext, List<CaravanInfo> dataList) {
        this.mContext = mContext;
        this.mDataList = dataList;

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_caravans_customer,
                    null);
        }

        // Get Item Data
        CaravanInfo itemInfo = (CaravanInfo) getItem(position);

        // Gas Image
        final ImageView ivCaravan = (ImageView) convertView.findViewById(R.id.ivCaravan);



        List<String> images = itemInfo.getImages();
        if (images != null && images.size() > 0) {
            mImageLoader.displayImage(itemInfo.getImages().get(0), ivCaravan, mImageOptions);
        } else {
            ivCaravan.setImageResource(R.drawable.ic_placeholder);
        }

        // Caravan Information
        TextView tvCaravanTitle = (TextView) convertView.findViewById(R.id.tvCaravanTitle);
        TextView tvCaravanDetails = (TextView) convertView.findViewById(R.id.tvCaravanDetails);

        tvCaravanTitle.setText(itemInfo.getName());
        tvCaravanDetails.setText(itemInfo.getDescription());
        //spinnerGasAmount.setText(itemInfo.getName());
        return convertView;
    }
}
