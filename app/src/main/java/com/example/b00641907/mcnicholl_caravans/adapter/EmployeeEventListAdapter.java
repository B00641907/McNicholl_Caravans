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

import java.util.List;

public class EmployeeEventListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EventInfo> mDataList;

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mImageOptions;

    public EmployeeEventListAdapter(Context mContext, List<EventInfo> dataList) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_event_employee,
                    null);
        }

        // Get Item Data
        EventInfo itemInfo = (EventInfo) getItem(position);

        // Gas Image
        final ImageView ivEvent = (ImageView) convertView.findViewById(R.id.ivEvent);
        mImageLoader.displayImage(itemInfo.getImage(), ivEvent, mImageOptions);

        // Gas Information
        TextView tvEventName = (TextView) convertView.findViewById(R.id.tvEventName);
        TextView tvEventTime = (TextView) convertView.findViewById(R.id.tvEventTime);
        TextView tvEventDescription = (TextView) convertView.findViewById(R.id.tvEventDescription);

        tvEventName.setText(itemInfo.getName());
        tvEventTime.setText(itemInfo.getTime());
        tvEventDescription.setText(itemInfo.getDescription());

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
                Intent intent = new Intent(mContext, EditEventActivity.class);
                intent.putExtra("event_info", mDataList.get(position));
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
            alertDialogBuilder.setMessage("Would you like to remove this event?")
                    .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                    ((EmployeeEventActivity) mContext).removeEvent(position);
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
