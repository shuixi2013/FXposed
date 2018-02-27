package me.zpp0196.fxposed;

import java.lang.ref.WeakReference;

import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by zpp0196 on 2018/2/27.
 */

class Settings {

    private static WeakReference<XSharedPreferences> xSharedPreferences = new WeakReference<>(null);

    private static XSharedPreferences getModuleSharedPreferences() {
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


    static boolean isCottonTheme() {
        return getModuleSharedPreferences().getBoolean("cotton_theme", false);
    }

    static boolean isCustomizeFont() {
        return getModuleSharedPreferences().getBoolean("system_font", false);
    }

    static String customizeFontPath() {
        return getModuleSharedPreferences().getString("system_font_path", "");
    }

    static boolean isEncryptSn() {
        return getModuleSharedPreferences().getBoolean("encrypt_sn", true);
    }

    static boolean isEncryptMEID() {
        return getModuleSharedPreferences().getBoolean("encrypt_meid", true);
    }

    static boolean isEncryptIMEI() {
        return getModuleSharedPreferences().getBoolean("encrypt_imei", true);
    }

    static boolean mzinstallerUnknown() {
        return getModuleSharedPreferences().getBoolean("mzinstaller_unknown", false);
    }

    static boolean mzinstallerConfirm() {
        return getModuleSharedPreferences().getBoolean("mzinstaller_confirm", true);
    }

    static boolean mzinstallerVirus() {
        return getModuleSharedPreferences().getBoolean("mzinstaller_virus", false);
    }

    static boolean mznfcpayRoot() {
        return getModuleSharedPreferences().getBoolean("mznfcpay_root", false);
    }

    static boolean mzupdateRoot() {
        return getModuleSharedPreferences().getBoolean("mzupdate_root", false);
    }

}
