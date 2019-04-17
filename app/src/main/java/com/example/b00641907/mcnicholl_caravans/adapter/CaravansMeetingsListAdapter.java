package com.example.b00641907.mcnicholl_caravans.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.b00641907.mcnicholl_caravans.R;
import com.example.b00641907.mcnicholl_caravans.model.CaravanMeeting;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class CaravansMeetingsListAdapter extends BaseAdapter {

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mImageOptions;
    private Context mContext;
    private List<CaravanMeeting> mDataList;

    public CaravansMeetingsListAdapter(Context mContext, List<CaravanMeeting> dataList) {
        this.mContext = mContext;
        this.mDataList = dataList;

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.items_caravans_meetings,
                    null);
        }

        // Get Item Data
        CaravanMeeting caravanMeeting = (CaravanMeeting) getItem(position);



        // Gas Information
        TextView tvCaravanTitle = (TextView) convertView.findViewById(R.id.tvCaravanTitle);
        TextView meeting_with = (TextView) convertView.findViewById(R.id.meeting_with);
        TextView meeting_time = (TextView) convertView.findViewById(R.id.meeting_time);

        tvCaravanTitle.setText(caravanMeeting.caravan_name);
        meeting_with.setText(String.format("Meeting with %s (%s )", caravanMeeting.uName, caravanMeeting.uEmail));
        meeting_time.setText("Scheduled for "+caravanMeeting.meeting_date);
        //spinnerGasAmount.setText(itemInfo.getName());
        return convertView;
    }
}


