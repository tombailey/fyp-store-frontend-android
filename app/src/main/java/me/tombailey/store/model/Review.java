package me.tombailey.store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tom on 05/12/2016.
 */

public class Review implements Parcelable {

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };


    private String id;
    private int rating;
    private String description;

    public Review(Parcel in) {
        this(in.readString(), in.readInt(), in.readString());
    }

    public Review(String id, int rating, String description) {
        this.id = id;
        this.rating = rating;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(rating);
        parcel.writeString(description);
    }
}
