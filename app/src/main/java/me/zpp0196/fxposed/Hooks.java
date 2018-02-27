package me.zpp0196.fxposed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;

/**
 * Created by zpp0196 on 2018/2/27.
 */

public class Hooks implements IXposedHookLoadPackage {

    private XC_LoadPackage.LoadPackageParam lpparam;
    private XC_MethodHook.MethodHookParam MzStoreAppInfo = null;

    private void findAndHookMethod(String className, String mothodName, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(className, lpparam.classLoader, mothodName, parameterTypesAndCallback);
        } catch (Throwable throwable) {
            // do nothing
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        this.lpparam = lpparam;
        updateModuleState();
        cottonTheme();
        customizeFont();
        settingEncrypt();
        installer();
        hookRoot();
    }

    private void updateModuleState() {
        if (isLoadPackage(BuildConfig.APPLICATION_ID)) {
            findAndHookMethod(SettingsActivity.class.getName(), "getActivatedModuleVersion", XC_MethodReplacement.returnConstant(BuildConfig.VERSION_CODE));
        }
    }

    private void cottonTheme() {
        if (Settings.isCottonTheme()) {
            // BootBroadcastReceiver
            findAndHookMethod("com.meizu.customizecenter.common.helper.BootBroadcastReceiver", "onReceive", Context.class, Intent.class, XC_MethodReplacement.returnConstant(null));

            // TrialService
            findAndHookMethod("com.meizu.customizecenter.common.theme.ThemeTrialService", "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));
            findAndHookMethod("com.meizu.customizecenter.common.font.FontTrialService", "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));

            if (isLoadPackage("com.meizu.customizecenter")) {
                // resetToSystemTheme
                // 6.11.1 & 6.12.1 & 6.13.1 & 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0 & 7.0.4
                findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", "b", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", "b", boolean.class, XC_MethodReplacement.returnConstant(null));

                // notification
                // 6.11.0 & 6.12.1
                findAndHookMethod("com.meizu.customizecenter.common.f.e", "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.f.e", "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
                // 6.13.1 & 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0
                findAndHookMethod("com.meizu.customizecenter.common.g.f", "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.g.f", "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
                // 7.0.4
                findAndHookMethod("com.meizu.customizecenter.common.h.g", "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.h.g", "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));

                // data/data/com.meizu.customizecenter/font/   system_font
                // 6.11.1 & 6.12.1 & 6.13.1 & 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0 & 7.0.4
                findAndHookMethod("com.meizu.customizecenter.common.font.c", "b", XC_MethodReplacement.returnConstant(false));

                // notification
                // 6.11.0 & 6.12.1
                findAndHookMethod("com.meizu.customizecenter.common.f.c", "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.f.c", "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
                // 6.13.1 & 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0
                findAndHookMethod("com.meizu.customizecenter.common.g.c", "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.g.c", "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
                // 7.0.4
                findAndHookMethod("com.meizu.customizecenter.common.h.c", "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.h.c", "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));

                // ThemeContentProvider query Unknown URI
                findAndHookMethod("com.meizu.customizecenter.common.dao.ThemeContentProvider", "query", Uri.class, String[].class, String.class, String[].class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        Object[] objs = param.args;
                        String Tag = "(ITEMS LIKE";
                        String Tag2 = "%zklockscreen;%";
                        String Tag3 = "%com.meizu.flyme.weather;%";
                        boolean result = false;
                        for (Object obj : objs) {
                            if (obj instanceof String && (((String) obj).contains(Tag) || obj.equals(Tag2) || obj.equals(Tag3))) {
                                result = true;
                            }
                        }
                        if (result) {
                            for (Object obj : objs) {
                                if (obj instanceof String[]) {
                                    for (int j = 0; j < ((String[]) obj).length; j++) {
                                        if (((String[]) obj)[j].contains("/storage/emulated/0/Customize/Themes")) {
                                            ((String[]) obj)[j] = "/storage/emulated/0/Customize%";
                                        } else if (((String[]) obj)[j].contains("/storage/emulated/0/Customize/TrialThemes")) {
                                            ((String[]) obj)[j] = "NONE";
                                        }
                                    }
                                }
                            }
                        }
                        super.beforeHookedMethod(param);
                    }
                });
            }
        }
    }

    private void customizeFont() {
        if (Settings.isCustomizeFont()) {
            File ttf = new File(Settings.customizeFontPath());

            if (!ttf.exists()) {
                return;
            }

            final Typeface typeface = Typeface.createFromFile(ttf);

            XposedBridge.hookAllConstructors(Paint.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param.thisObject instanceof Paint) {
                        ((Paint) param.thisObject).setTypeface(typeface);
                    }
                }
            });

            findAndHookMethod(Typeface.class.getName(), "defaultFromStyle", int.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return typeface;
                }
            });

            findAndHookMethod("android.content.res.flymetheme.FlymeFontsHelper", "hasFlymeTypeface", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });

            findAndHookMethod(Paint.class.getName(), "setTypeface", Typeface.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (isSystemTypeface((Typeface) param.args[0])) {
                        param.args[0] = typeface;
                    }
                }
            });
        }
    }

    private boolean isSystemTypeface(Typeface typeface) {
        return typeface == null || typeface == Typeface.DEFAULT || typeface == Typeface.DEFAULT_BOLD || typeface == Typeface.SANS_SERIF || typeface == Typeface.SERIF || typeface == Typeface.MONOSPACE;
    }

    private void settingEncrypt() {
        if (isLoadPackage("com.android.settings")) {
            findAndHookMethod("com.meizu.settings.deviceinfo.FlymeDeviceInfoPreference", "init", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (Settings.isEncryptSn())
                        setObjectField(param, "mSerialNumberSummary", encrypt(getField(param, "mSerialNumberSummary")));
                    if (Settings.isEncryptMEID())
                        setObjectField(param, "mMeidSummary", encrypt(getField(param, "mMeidSummary")));
                    if (Settings.isEncryptIMEI()) {
                        setObjectField(param, "mDeviceIdOneSummary", encrypt(getField(param, "mDeviceIdOneSummary")));
                        setObjectField(param, "mDeviceIdTwoSummary", encrypt(getField(param, "mDeviceIdTwoSummary")));
                    }
                }

                private String encrypt(String text) {
                    String start = text.substring(0, 3);
                    String end = text.substring(text.length() - 3, text.length());
                    StringBuilder center = new StringBuilder();
                    for (int i = 0; i <= text.length() - 6; i++) {
                        center.append("*");
                    }
                    return start + center.toString() + end;
                }
            });
        }
    }

    private void installer() {
        if (isLoadPackage("com.android.packageinstaller")) {
            if (Settings.mzinstallerUnknown()) {
                findAndHookMethod("com.meizu.permissioncommon.AppInfoUtil", "isSystemApp", Context.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(true);
                    }
                });

                // scan time
                findAndHookMethod("com.android.packageinstaller.PackageInstallerActivity", "setVirusCheckTime", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        setObjectField(param, "SCAN_TIMEOUT_TIME", 0);
                    }
                });

                if (Settings.mzinstallerConfirm()) {
                    findAndHookConstructor("com.meizu.safe.security.bean.MzStoreAppInfo", lpparam.classLoader, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            MzStoreAppInfo = param;
                        }
                    });

                    // confirm button
                    findAndHookMethod("com.android.packageinstaller.PackageInstallerActivity", "onScanFinish", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            setObjectField(MzStoreAppInfo, "showConfirm", Settings.mzinstallerConfirm());
                        }
                    });
                }
            }

            if (Settings.mzinstallerVirus()) {
                findAndHookMethod("com.android.packageinstaller.PackageInstallerActivity", "startSafeCheckService", String.class, XC_MethodReplacement.returnConstant(null));
            }
        }
    }

    private void hookRoot() {
        if (isLoadPackage("com.meizu.customizecenter") && Settings.isCottonTheme()) {
            // device_states | doCheckState
            // 6.11.0 & 6.13.1
            findAndHookMethod("com.meizu.customizecenter.h.al", "h", Context.class, XC_MethodReplacement.returnConstant(0));
            // 6.12.1
            findAndHookMethod("com.meizu.customizecenter.g.ak", "h", Context.class, XC_MethodReplacement.returnConstant(0));
            // 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0
            findAndHookMethod("com.meizu.customizecenter.h.am", "h", Context.class, XC_MethodReplacement.returnConstant(0));
            // 7.0.4
            findAndHookMethod("com.meizu.customizecenter.i.am", "h", Context.class, XC_MethodReplacement.returnConstant(0));
        }

        if (isLoadPackage("com.meizu.mznfcpay") && Settings.mznfcpayRoot()) {
            findAndHookMethod("com.meizu.cloud.a.a.a", "c", Context.class, XC_MethodReplacement.returnConstant(false));
        }

        if (isLoadPackage("com.meizu.flyme.update") && Settings.mzupdateRoot()) {
            findAndHookMethod("com.meizu.cloud.a.a.a", "b", Context.class, XC_MethodReplacement.returnConstant(false));
        }
    }

    private boolean isLoadPackage(String packagename) {
        return (lpparam.packageName.equals(packagename));
    }

    private <T> T getField(XC_MethodHook.MethodHookParam param, String name) {
        return (T) XposedHelpers.getObjectField(param.thisObject, name);
    }

    private void setObjectField(XC_MethodHook.MethodHookParam param, String fieldName, Object value) {
        try {
            XposedHelpers.setObjectField(param.thisObject, fieldName, value);
        } catch (Exception e) {
            // do nothing
        }
    }
}
