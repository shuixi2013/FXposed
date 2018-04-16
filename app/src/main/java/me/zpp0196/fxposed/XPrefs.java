package me.zpp0196.fxposed;

import java.lang.ref.WeakReference;

import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by zpp0196 on 2018/2/27.
 */

class XPrefs {

    private static WeakReference<XSharedPreferences> xSharedPreferences = new WeakReference<>(null);

    static XSharedPreferences getPrefs() {
        XSharedPreferences preferences = xSharedPreferences.get();
        if (preferences == null) {
            preferences = new XSharedPreferences(BuildConfig.APPLICATION_ID);
            preferences.makeWorldReadable();
            xSharedPreferences = new WeakReference<>(preferences);
        } else {
            preferences.reload();
        }
        return preferences;
    }
}
