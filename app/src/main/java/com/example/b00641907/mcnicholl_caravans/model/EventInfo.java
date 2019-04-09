package com.example.b00641907.mcnicholl_caravans.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EventInfo implements Parcelable {

    /*** Don't Change Variable Name and Function Name ***/

    private String image;
    private String name, description;
    private String time;
    private int lock;
    private String key;

    public EventInfo() {
    }

    protected EventInfo(Parcel in) {
        image = in.readString();
        name = in.readString();
        description = in.readString();
        time = in.readString();
        lock = in.readInt();

        key = in.readString();
    }

    public static final Creator<EventInfo> CREATOR = new Creator<EventInfo>() {
        @Override
        public EventInfo createFromParcel(Parcel in) {
            return new EventInfo(in);
        }

        @Override
        public EventInfo[] newArray(int size) {
            return new EventInfo[size];
        }
    };

    // Event Image
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    // Event Name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Event Description
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // Event Time
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    // Lock
    public int getLock() {
        return lock;
    }
    public void setLock(int lock) {
        this.lock = lock;
    }

    // NodeKey
    public String retrieveNodeKey() {
        return key;
    }
    public void saveNodeKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(time);
        dest.writeInt(lock);

        dest.writeString(key);
    }
}
