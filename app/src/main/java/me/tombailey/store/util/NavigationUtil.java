package me.tombailey.store.util;

import android.app.Activity;
import android.content.Intent;

import me.tombailey.store.MainActivity;

/**
 * Created by tomba on 16/03/2017.
 */

public class NavigationUtil {

    public static void goBackToHome(Activity fromWhere) {
        Intent goBackToMainIntent = new Intent(fromWhere, MainActivity.class);
        goBackToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        fromWhere.startActivity(goBackToMainIntent);
        fromWhere.finish();
    }

}
