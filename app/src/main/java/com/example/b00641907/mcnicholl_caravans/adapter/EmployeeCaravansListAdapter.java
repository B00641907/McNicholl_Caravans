package com.example.b00641907.mcnicholl_caravans.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.b00641907.mcnicholl_caravans.EditCaravanActivity;
import com.example.b00641907.mcnicholl_caravans.EmployeeCaravansActivity;
import com.example.b00641907.mcnicholl_caravans.model.CaravanInfo;

import java.util.List;

public class EmployeeCaravansListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CaravanInfo> mDataList;

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mImageOptions;

    public EmployeeCaravansListAdapter(Context mContext, List<CaravanInfo> dataList) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_caravans_employee,
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

        // Gas Information
        TextView tvCaravanTitle = (TextView) convertView.findViewById(R.id.tvCaravanTitle);
        TextView tvCaravanDetails = (TextView) convertView.findViewById(R.id.tvCaravanDetails);

        tvCaravanTitle.setText(itemInfo.getName());
        tvCaravanDetails.setText(itemInfo.getDescription());

        View btnEdit = convertView.findViewById(R.id.btnEdit);
        View btnRemove = convertView.findViewById(R.id.btnRemove);

        btnEdit.setTag(position);
        btnEdit.setOnClickListener(btnEditListner);


        btnRemove.setTag(position);
        btnRemove.setOnClickListener(btnRemoveListner);
        return convertView;
    }

    View.OnClickListener btnEditListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            try {
                Intent intent = new Intent(mContext, EditCaravanActivity.class);
                intent.putExtra("caravan_info", mDataList.get(position));
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener btnRemoveListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();

            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            alertDialogBuilder.setTitle("Confirm remove");
            alertDialogBuilder.setMessage("Would you like to remove this caravan?")
                    .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                    ((EmployeeCaravansActivity) mContext).removeCaravan(position);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    };
}
