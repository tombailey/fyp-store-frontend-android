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


    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String FEATURE_GRAPHIC = "featureGraphic";
    private static final String ICON_LINK = "iconLink";
    private static final String DOWNLOAD_COUNT = "downloadCount";
    private static final String CURRENT_VERSION_NUMBER = "currentVersionNumber";
    private static final String CURRENT_VERSION_DATE = "currentVersionDate";
    private static final String SCREENSHOTS = "screenshots";
    private static final String CATEGORIES = "categories";


    private String id;
    private String name;
    private String description;

    private String iconLink;
    private String featureGraphicLink;

    private long downloadCount;

    private long currentVersionNumber;
    private String currentVersionDate;

    private String[] screenshotLinks;

    private String[] categories;

    private Review[] reviews;


    public static App fromJson(JSONObject jsonObject) throws JSONException {
        JSONArray screenshotsJson = jsonObject.getJSONArray(SCREENSHOTS);
        String[] screenshots = new String[screenshotsJson.length()];
        for (int index = 0; index < screenshotsJson.length(); index++) {
            screenshots[index] = screenshotsJson.getString(index);
        }

        JSONArray categoriesJson = jsonObject.getJSONArray(CATEGORIES);
        String[] categories = new String[categoriesJson.length()];
        for (int index = 0; index < categoriesJson.length(); index++) {
            categories[index] = categoriesJson.getString(index);
        }

        return new App(jsonObject.getString(ID), jsonObject.getString(NAME),
                jsonObject.getString(DESCRIPTION), jsonObject.getString(FEATURE_GRAPHIC),
                jsonObject.getString(ICON_LINK), jsonObject.getLong(DOWNLOAD_COUNT),
                jsonObject.getLong(CURRENT_VERSION_NUMBER),
                jsonObject.getString(CURRENT_VERSION_DATE), screenshots, categories);
    }


    public App(Parcel parcel) {
        this(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(),
                parcel.readString(), parcel.readLong(), parcel.readLong(), parcel.readString(),
                parcel.createStringArray(), parcel.createStringArray());

        //TODO: handle reviews
        //parcel.createTypedArray(Review.CREATOR));
    }

    public App(String id, String name, String description, String featureGraphicLink,
                String iconLink, long downloadCount, long currentVersionNumber, String currentVersionDate,
                String[] screenshotLinks, String[] categories) {
        this(id, name, description, featureGraphicLink, iconLink, downloadCount,
                currentVersionNumber, currentVersionDate, screenshotLinks, categories,
                new Review[0]);
    }

    public App(String id, String name, String description, String featureGraphicLink,
               String iconLink, long downloadCount, long currentVersionNumber, String currentVersionDate,
               String[] screenshotLinks, String[] categories, Review[] reviews) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.featureGraphicLink = featureGraphicLink;
        this.iconLink = iconLink;

        this.downloadCount = downloadCount;

        this.currentVersionNumber = currentVersionNumber;
        this.currentVersionDate = currentVersionDate;

        this.screenshotLinks = screenshotLinks;

        this.categories = categories;

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

    public String getIconLink() {
        return iconLink;
    }

    public String getFeatureGraphicLink() {
        return featureGraphicLink;
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

    public String[] getScreenshotLinks() {
        return screenshotLinks;
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

        parcel.writeString(iconLink);
        parcel.writeString(featureGraphicLink);

        parcel.writeLong(downloadCount);

        parcel.writeLong(currentVersionNumber);
        parcel.writeString(currentVersionDate);

        parcel.writeStringArray(screenshotLinks);

        parcel.writeStringArray(categories);


        parcel.writeTypedArray(reviews, 0);
    }
}
