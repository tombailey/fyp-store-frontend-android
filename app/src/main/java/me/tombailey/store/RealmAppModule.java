package me.tombailey.store;

import io.realm.annotations.RealmModule;
import me.tombailey.store.model.InstalledApp;

/**
 * Created by tomba on 09/03/2017.
 */

@RealmModule(classes = {InstalledApp.class})
public class RealmAppModule {
}
