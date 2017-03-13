package me.tombailey.store.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 28/11/2016.
 */

public class App implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new App(parcel);
        }

        @Override
        public Object[] newArray(int i) {
            return new App[i];
        }
    };


    private static final String ID = "_id";

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    private static final String DOWNLOAD_COUNT = "downloadCount";

    private static final String CURRENT_VERSION = "currentVersion";
    private static final String NUMBER = "number";
    private static final String DATE = "date";

    private static final String SCREENSHOT_COUNT = "screenshotCount";

    private static final String RATING = "rating";

    private static final String CATEGORIES = "categories";


    private String id;
    private String name;
    private String description;

    private long downloadCount;

    private long currentVersionNumber;
    private String currentVersionDate;

    private int screenshotCount;

    private double rating;

    private String[] categories;

    private Review[] reviews;


    public static App fromJson(JSONObject jsonObject) throws JSONException {
        JSONArray categoriesJson = jsonObject.getJSONArray(CATEGORIES);
        String[] categories = new String[categoriesJson.length()];
        for (int index = 0; index < categoriesJson.length(); index++) {
            categories[index] = categoriesJson.getString(index);
        }

        JSONObject currentVersion = jsonObject.getJSONObject(CURRENT_VERSION);

        return new App(jsonObject.getString(ID), jsonObject.getString(NAME),
                jsonObject.getString(DESCRIPTION), jsonObject.getLong(DOWNLOAD_COUNT),
                currentVersion.getLong(NUMBER), currentVersion.getString(DATE),
                jsonObject.getInt(SCREENSHOT_COUNT), jsonObject.getDouble(RATING), categories);
    }


    public App(Parcel parcel) {
        this(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readLong(),
                parcel.readLong(), parcel.readString(), parcel.readInt(), parcel.readDouble(),
                parcel.createStringArray(), parcel.createTypedArray(Review.CREATOR));
    }

    public App(String id, String name, String description, long downloadCount,
               long currentVersionNumber, String currentVersionDate, int screenshotCount,
               double rating, String[] categories) {
        this(id, name, description, downloadCount, currentVersionNumber, currentVersionDate,
                screenshotCount, rating, categories, new Review[0]);
    }

    public App(String id, String name, String description, long downloadCount,
               long currentVersionNumber, String currentVersionDate, int screenshotCount,
               double rating, String[] categories, Review[] reviews) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.downloadCount = downloadCount;

        this.currentVersionNumber = currentVersionNumber;
        this.currentVersionDate = currentVersionDate;

        this.screenshotCount = screenshotCount;

        this.categories = categories;

        this.rating = rating;

        this.reviews = reviews;
    }



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadLink() {
        return "http://q6yl3es3js7j3gm3.onion/api/applications/" + id + "/versions/" + currentVersionNumber;
    }

    public String getIconLink() {
        return "http://q6yl3es3js7j3gm3.onion/api/applications/" + id + "/icon";
    }

    public String getFeatureGraphicLink() {
        return "http://q6yl3es3js7j3gm3.onion/api/applications/" + id + "/featureGraphic";
    }

    public long getDownloadCount() {
        return downloadCount;
    }

    public long getCurrentVersionNumber() {
        return currentVersionNumber;
    }

    public String getCurrentVersionDate() {
        return currentVersionDate;
    }

    public double getRating() {
        return rating;
    }

    public String getScreenshotLink(int screenshotNumber) {
        return "http://q6yl3es3js7j3gm3.onion/api/applications/" + id + "/screenshots/" + screenshotNumber;
    }

    public int getScreenshotCount() {
        return screenshotCount;
    }

    public String[] getCategories() {
        return categories;
    }

    public Review[] getReviews() {
        return reviews;
    }

    public void setReviews(Review[] reviews) {
        this.reviews = reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(description);

        parcel.writeLong(downloadCount);

        parcel.writeLong(currentVersionNumber);
        parcel.writeString(currentVersionDate);

        parcel.writeInt(screenshotCount);
        parcel.writeDouble(rating);

        parcel.writeStringArray(categories);

        parcel.writeTypedArray(reviews, 0);
    }
}
