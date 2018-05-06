package me.zpp0196.fxposed;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

import java.io.File;

/**
 * Created by zpp0196 on 2018/2/27.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private int getActivatedModuleVersion() {
        return -1;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWorldReadable();
        addPreferencesFromResource(R.xml.pref_main);
        checkState();
        initData();
    }

    private void initData() {
        EditTextPreference fontPath = (EditTextPreference) findPreference("system_font_path");
        fontPath.setSummary(fontPath.getText());
        fontPath.setOnPreferenceChangeListener(this);
        SwitchPreference desktopIcon = (SwitchPreference) findPreference("desktop_icon");
        desktopIcon.setOnPreferenceChangeListener(this);
        desktopIcon.setChecked(getEnable());
        findPreference("version").setSummary(String.format("%s(%s)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        findPreference("version").setOnPreferenceClickListener(this);
        findPreference("author").setOnPreferenceClickListener(this);
        findPreference("donate").setOnPreferenceClickListener(this);
        findPreference("github").setOnPreferenceClickListener(this);
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private ComponentName getAlias() {
        return new ComponentName(this, String.format("%s.SettingsActivityAlias", BuildConfig.APPLICATION_ID));
    }

    private boolean getEnable() {
        return getPackageManager().getComponentEnabledSetting(getAlias()) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    private void checkState() {
        if (getActivatedModuleVersion() < 0) {
            showNotActive();
        } else if (getPrefs().getInt("version_code", -1) < BuildConfig.VERSION_CODE) {
            showLicenseDialog();
        }
    }

    private void showNotActive() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("模块未激活，请先激活模块并重启手机！")
                .setPositiveButton("激活", (dialog, id) -> openXposed())
                .setNegativeButton("取消", null)
                .show();
    }

    private void showLicenseDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("该模块仅供学习交流使用，可以转载但请勿用于商业用途！\n\n使用该模块带来的任何风险和后果自行承担！\n\nPS：Flyme7 好像有部分功能不能用\n")
                .setPositiveButton("同意", (dialog, which) -> getPrefs().edit().putInt("version_code", BuildConfig.VERSION_CODE).apply())
                .setNegativeButton("退出", (dialog, which) -> finish())
                .show();
    }

    private void openXposed() {
        if (isXposedInstalled()) {
            Intent intent = new Intent("de.robv.android.xposed.installer.OPEN_SECTION");
            if (getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
                intent = getPackageManager().getLaunchIntentForPackage("de.robv.android.xposed.installer");
            }
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("section", "modules")
                        .putExtra("fragment", 1)
                        .putExtra("module", BuildConfig.APPLICATION_ID);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "未安装 XposedInstaller !", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isXposedInstalled() {
        try {
            getPackageManager().getApplicationInfo("de.robv.android.xposed.installer", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    @SuppressLint({"SetWorldReadable", "WorldReadableFiles"})
    private void setWorldReadable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File dataDir = new File(getApplicationInfo().dataDir);
            File prefsDir = new File(dataDir, "shared_prefs");
            File prefsFile = new File(prefsDir, getPreferenceManager().getSharedPreferencesName() + ".xml");
            if (prefsFile.exists()) {
                for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
                    file.setReadable(true, false);
                    file.setExecutable(true, false);
                }
            }
        } else {
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setWorldReadable();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof EditTextPreference) {
            preference.setSummary(newValue.toString());
        }
        switch (preference.getKey()) {
            case "desktop_icon":
                getPackageManager().setComponentEnabledSetting(getAlias(), getEnable() ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                break;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "version":
                openCoolApk("me.zpp0196.fxposed");
                break;
            case "author":
                openUrl("https://weibo.com/u/5844596591");
                break;
            case "donate":
                openAlipay("FKX03149H8YOUWESHOCEC6");
                break;
            case "github":
                openUrl("https://github.com/zpp0196/FXposed");
                break;
        }
        return false;
    }

    public void openAlipay(String qrcode) {
        try {
            getPackageManager().getPackageInfo("com.eg.android.AlipayGphone", PackageManager.GET_ACTIVITIES);
            openUrl("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https://qr.alipay.com/" + qrcode + "%3F_s%3Dweb-other&_t=");
        } catch (PackageManager.NameNotFoundException e) {
            openUrl("https://mobilecodec.alipay.com/client_download.htm?qrcode=" + qrcode);
        }
    }

    @SuppressLint("WrongConstant")
    private void openCoolApk(String packageName) {
        try {
            String str = "market://details?id=" + packageName;
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(str));
            intent.setPackage("com.coolapk.market");
            intent.setFlags(0x10000000);
            startActivity(intent);
        } catch (Exception e) {
            openUrl("http://www.coolapk.com/apk/" + packageName);
        }
    }

    private void openUrl(String uri) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(uri);
        intent.setData(content_url);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}