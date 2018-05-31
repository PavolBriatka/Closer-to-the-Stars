package com.example.pavol.closertostars;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pavol on 25/11/2017.
 */

public class ItemModel implements Parcelable {

    public final String mMediaSource;
    public final String mDescription;
    public final String mTitle;
    public final String mMediaType;
    public final String mDate;

    public ItemModel(String imgSrc, String description, String title, String mediaType, String date) {
        mMediaSource = imgSrc;
        mDescription = description;
        mTitle = title;
        mMediaType = mediaType;
        mDate = date;
    }

    private ItemModel(Parcel parcel) {
        mMediaSource = parcel.readString();
        mDescription = parcel.readString();
        mTitle = parcel.readString();
        mMediaType = parcel.readString();
        mDate = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mMediaSource);
        parcel.writeString(mDescription);
        parcel.writeString(mTitle);
        parcel.writeString(mMediaType);
        parcel.writeString(mDate);
    }

    public static Parcelable.Creator<ItemModel> CREATOR = new Parcelable.Creator<ItemModel>() {
        @Override
        public ItemModel createFromParcel(Parcel parcel) {
            return new ItemModel(parcel);
        }

        @Override
        public ItemModel[] newArray(int i) {
            return new ItemModel[i];
        }
    };
}
