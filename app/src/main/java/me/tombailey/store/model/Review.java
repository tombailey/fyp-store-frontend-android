package me.tombailey.store.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

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


    private static final String ID = "_id";
    private static final String STARS = "stars";
    private static final String DESCRIPTION = "description";
    private static final String DATE = "date";


    private String id;
    private int rating;
    private String description;
    private String date;


    public static Review fromJson(JSONObject jsonObject) throws JSONException {
        return new Review(jsonObject.getString(ID), jsonObject.getInt(STARS),
                jsonObject.getString(DESCRIPTION), jsonObject.getString(DATE));
    }


    public Review(Parcel in) {
        this(in.readString(), in.readInt(), in.readString(), in.readString());
    }

    public Review(String id, int rating, String description, String date) {
        this.id = id;
        this.rating = rating;
        this.description = description;
        this.date = date;
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

    public String getDate() {
        return date;
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
        parcel.writeString(date);
    }
}
