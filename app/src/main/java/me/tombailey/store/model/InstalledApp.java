package me.tombailey.store.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by tomba on 07/03/2017.
 */

public class InstalledApp extends RealmObject {

    @PrimaryKey
    private String mAppId;

    @Required
    private Long mVersionNumber;

    public InstalledApp() {

    }

    public String getAppId() {
        return mAppId;
    }

    public void setAppId(String appId) {
        mAppId = appId;
    }

    public Long getVersionNumber() {
        return mVersionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        mVersionNumber = versionNumber;
    }

    public static InstalledApp newInstance(String appId, Long versionNumber) {
        InstalledApp installedApp = new InstalledApp();
        installedApp.setAppId(appId);
        installedApp.setVersionNumber(versionNumber);
        return installedApp;
    }
}
