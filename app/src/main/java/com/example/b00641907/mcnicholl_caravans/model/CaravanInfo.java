package com.example.b00641907.mcnicholl_caravans.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CaravanInfo implements Parcelable {

    /*** Don't Change Variable Name and Function Name ***/
    private String name;
    private String description;
    private int price;
    private int status;
    private int lock;
    private List<String> images;
    private String folder;

    private String key;

    public CaravanInfo() {
    }

    // Name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Description
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // Price
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    // Status
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    // Lock
    public int getLock() {
        return lock;
    }
    public void setLock(int lock) {
        this.lock = lock;
    }

    // Images
    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }

    // ImageFolder
    public String getFolder() {
        return folder;
    }
    public void setFolder(String folder) {
        this.folder = folder;
    }

    // Node Key
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

        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeInt(status);
        dest.writeInt(lock);
        dest.writeStringList(images);
        dest.writeString(folder);

        dest.writeString(key);
    }

    protected CaravanInfo(Parcel in) {
        name = in.readString();
        description = in.readString();
        price = in.readInt();
        status = in.readInt();
        lock = in.readInt();
        images = in.createStringArrayList();
        folder = in.readString();

        key = in.readString();
    }

    public static final Creator<CaravanInfo> CREATOR = new Creator<CaravanInfo>() {
        @Override
        public CaravanInfo createFromParcel(Parcel in) {
            return new CaravanInfo(in);
        }

        @Override
        public CaravanInfo[] newArray(int size) {
            return new CaravanInfo[size];
        }
    };
}