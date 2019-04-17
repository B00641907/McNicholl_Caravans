package com.example.b00641907.mcnicholl_caravans.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.b00641907.mcnicholl_caravans.R;
import com.example.b00641907.mcnicholl_caravans.model.GasInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class CustomerGasListAdapter extends BaseAdapter {

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mImageOptions;
    private Context mContext;
    private List<GasInfo> mDataList;
    private List<GasInfo> cartList;
    QuantitySelectedListener  quantitySelectedListener;
    public CustomerGasListAdapter(Context mContext, List<GasInfo> dataList,List<GasInfo> cartList,QuantitySelectedListener  quantitySelectedListener) {
        this.mContext = mContext;
        this.mDataList = dataList;
        this.cartList = cartList;
        this.quantitySelectedListener = quantitySelectedListener;

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
    public View getView(int position,  View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gas_customer,
                    null);
        }

        // Get Item Data
        final GasInfo itemInfo = (GasInfo) getItem(position);

        // Gas Image
        final ImageView ivGas = (ImageView) convertView.findViewById(R.id.ivGas);

        mImageLoader.displayImage(itemInfo.getImage(), ivGas, mImageOptions);

        // Gas Information
        final TextView tvGasTitle = (TextView) convertView.findViewById(R.id.tvGasTitle);
        final TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
        final Spinner spinnerGasAmount = (Spinner) convertView.findViewById(R.id.spinnerGasAmount);

        spinnerGasAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(quantitySelectedListener!=null){

                    quantitySelectedListener.onQuantitySelected(position,itemInfo,spinnerGasAmount);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvGasTitle.setText(itemInfo.getName());
        tvPrice.setText(String.format("£%d / %dKg", itemInfo.getPricePerBox(), itemInfo.getWeightPerBox()));

        //spinnerGasAmount.setText(itemInfo.getName());
        return convertView;
    }

    public interface QuantitySelectedListener{
        void onQuantitySelected(int quantity,GasInfo itemInfo ,Spinner spinnerGasAmount);
    }
}
