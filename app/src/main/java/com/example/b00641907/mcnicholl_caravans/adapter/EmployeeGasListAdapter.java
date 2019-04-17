package com.example.b00641907.mcnicholl_caravans.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.b00641907.mcnicholl_caravans.R;
import com.example.b00641907.mcnicholl_caravans.model.GasInfo;
import com.example.b00641907.mcnicholl_caravans.EditGasActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class EmployeeGasListAdapter extends BaseAdapter {

    private Context mContext;
    private List<GasInfo> mDataList;

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mImageOptions;

    public EmployeeGasListAdapter(Context mContext, List<GasInfo> dataList) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gas_employee,
                    null);
        }

        // Get Item Data
        GasInfo itemInfo = (GasInfo) getItem(position);

        // Gas Image
        final ImageView ivGas = (ImageView) convertView.findViewById(R.id.ivGas);

        mImageLoader.displayImage(itemInfo.getImage(), ivGas, mImageOptions);

        // Gas Information
        TextView tvGasTitle = (TextView) convertView.findViewById(R.id.tvGasTitle);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
        ImageView btnEdit = (ImageView) convertView.findViewById(R.id.btnEdit);

        tvGasTitle.setText(itemInfo.getName());
        tvPrice.setText(String.format("£%d / %dKg", itemInfo.getPricePerBox(), itemInfo.getWeightPerBox()));

        btnEdit.setTag(position);
        btnEdit.setOnClickListener(editBtnClickListener);
        return convertView;
    }

    View.OnClickListener editBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            try {
                Intent intent = new Intent(mContext, EditGasActivity.class);
                intent.putExtra("gas_info", mDataList.get(position));
                mContext.startActivity(intent);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
}