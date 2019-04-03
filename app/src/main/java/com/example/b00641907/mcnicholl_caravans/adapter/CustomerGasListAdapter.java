package com.example.b00641907.mcnicholl_caravans.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class CustomerGasListAdapter extends BaseAdapter {

    private Context mContext;
    private List<GasInfo> mDataList;

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mImageOptions;

    public CustomerGasListAdapter(Context mContext, List<GasInfo> dataList) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gas_customer,
                    null);
        }

        // Get Item Data
        GasInfo itemInfo = (GasInfo) getItem(position);


        mImageLoader.displayImage(itemInfo.getImage(), ivGas, mImageOptions);

        // Gas Information
        TextView tvGasTitle = (TextView) convertView.findViewById(R.id.tvGasTitle);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
        Spinner spinnerGasAmount = (Spinner) convertView.findViewById(R.id.spinnerGasAmount);

        tvGasTitle.setText(itemInfo.getName());
        tvPrice.setText(String.format("£%d / %dKg", itemInfo.getPricePerBox(), itemInfo.getWeightPerBox()));


        return convertView;
    }
}
