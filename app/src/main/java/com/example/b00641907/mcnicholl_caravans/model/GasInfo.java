package com.example.b00641907.mcnicholl_caravans.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GasInfo implements Parcelable {

    // Variable Name and Function Name
    private String image;
    private String reference;
    private String name, description;
    private int weightPerBox, pricePerBox;
    private int amountSelected;

    private int lock;

    private String key;

    public GasInfo() {
    }

    public GasInfo(String gasImage, String gasName, String description, int weightPerBox, int pricePerBox) {
        this.image = gasImage;
        this.name = gasName;
        this.description = description;

        this.weightPerBox = weightPerBox;
        this.pricePerBox = pricePerBox;
    }

    protected GasInfo(Parcel in) {
        image = in.readString();
        reference = in.readString();
        name = in.readString();
        description = in.readString();
        weightPerBox = in.readInt();
        pricePerBox = in.readInt();
        amountSelected = in.readInt();
        lock = in.readInt();
        key = in.readString();
    }

    public static final Creator<GasInfo> CREATOR = new Creator<GasInfo>() {
        @Override
        public GasInfo createFromParcel(Parcel in) {
            return new GasInfo(in);
        }

        @Override
        public GasInfo[] newArray(int size) {
            return new GasInfo[size];
        }
    };

    // Gas Image
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    // Gas Image Reference
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    // Gas Name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Gas Description
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // Gas Weight
    public int getWeightPerBox() {
        return weightPerBox;
    }
    public void setWeightPerBox(int weightPerBox) {
        this.weightPerBox = weightPerBox;
    }

    // Price Per Box
    public int getPricePerBox() {
        return pricePerBox;
    }
    public void setPricePerBox(int pricePerBox) {
        this.pricePerBox = pricePerBox;
    }

    // Amount
    public int getSelectedCnt() {
        return amountSelected;
    }
    public void setSelectedCnt(int amountSelected) {
        this.amountSelected = amountSelected;
    }

    // Lock
    public int getLock() {
        return lock;
    }
    public void setLock(int lock) {
        this.lock = lock;
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
        dest.writeString(image);
        dest.writeString(reference);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(weightPerBox);
        dest.writeInt(pricePerBox);
        dest.writeInt(amountSelected);
        dest.writeInt(lock);
        dest.writeString(key);
    }
}
