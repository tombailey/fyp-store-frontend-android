package me.tombailey.http;

/**
 * Created by Tom on 20/01/2017.
 */
public class Header {

    private String mName;
    private String mValue;

    public Header(String name, String value) {
        mName = name;
        mValue = value;
    }

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }
}
