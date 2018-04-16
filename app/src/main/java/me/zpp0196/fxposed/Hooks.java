package me.zpp0196.fxposed;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;

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
        } catch (Throwable t) {
            // do nothing
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        this.lpparam = lpparam;
        updateModuleState();
        customizeFont();
        settingEncrypt();
        installer();
        hideRoot();
    }

    private void updateModuleState() {
        if (!isLoadPackage(BuildConfig.APPLICATION_ID)) return;
        findAndHookMethod(SettingsActivity.class.getName(), "getActivatedModuleVersion", XC_MethodReplacement.returnConstant(BuildConfig.VERSION_CODE));
    }

    private void customizeFont() {
        if (!getBool("system_font", false)) return;

        try {
            File ttf = new File(XPrefs.getPrefs().getString("system_font_path", ""));
            if (!ttf.exists()) return;

            Typeface typeface = Typeface.createFromFile(ttf);

            XposedBridge.hookAllConstructors(Paint.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    ((Paint) param.thisObject).setTypeface(typeface);
                }
            });

            findAndHookMethod(Typeface.class.getName(), "defaultFromStyle", int.class, XC_MethodReplacement.returnConstant(typeface));
            findAndHookMethod("android.content.res.flymetheme.FlymeFontsHelper", "hasFlymeTypeface", XC_MethodReplacement.returnConstant(false));

            findAndHookMethod(Paint.class.getName(), "setTypeface", Typeface.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (isSystemTypeface((Typeface) param.args[0])) {
                        param.args[0] = typeface;
                    }
                }
            });
        } catch (Throwable t) {
            // do nothing
        }
    }

    private void hideRoot() {
        if (isLoadPackage("com.meizu.mznfcpay") && getBool("root_mzNfcPay", false)) {
            findAndHookMethod("com.meizu.cloud.a.a.a", "c", Context.class, XC_MethodReplacement.returnConstant(false));
        }

        if (isLoadPackage("com.meizu.flyme.update") && getBool("root_mzUpdate", false)) {
            findAndHookMethod("com.meizu.cloud.a.a.a", "b", Context.class, XC_MethodReplacement.returnConstant(false));
        }

        if (isLoadPackage("com.meizu.customizecenter") && getBool("root_theme", false)) {
            findAndHookMethod("com.meizu.customizecenter.h.al", "h", Context.class, XC_MethodReplacement.returnConstant(0));
            findAndHookMethod("com.meizu.customizecenter.g.ak", "h", Context.class, XC_MethodReplacement.returnConstant(0));
            findAndHookMethod("com.meizu.customizecenter.h.am", "h", Context.class, XC_MethodReplacement.returnConstant(0));
            findAndHookMethod("com.meizu.customizecenter.i.am", "h", Context.class, XC_MethodReplacement.returnConstant(0));
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
                    if (getBool("encrypt_sn", true))
                        encrypt(param.thisObject, "mSerialNumberSummary");
                    if (getBool("encrypt_meid", true)) encrypt(param.thisObject, "mMeidSummary");
                    if (getBool("encrypt_imei", true))
                        encrypt(param.thisObject, "mDeviceIdOneSummary");
                    if (getBool("encrypt_imei", true))
                        encrypt(param.thisObject, "mDeviceIdTwoSummary");
                }

                private void encrypt(Object object, String fieldName) {
                    try {
                        String text = (String) getField(object, fieldName);
                        if (text == null) return;
                        String start = text.substring(0, 3);
                        String end = text.substring(text.length() - 3, text.length());
                        StringBuilder center = new StringBuilder();
                        for (int i = 0; i <= text.length() - 6; i++) {
                            center.append("*");
                        }
                        setObjectField(object, fieldName, start + center.toString() + end);
                    } catch (Throwable t) {
                        XposedBridge.log(t);
                    }
                }
            });
        }
    }

    private void installer() {
        if (isLoadPackage("com.android.packageinstaller")) {
            if (getBool("mzInstaller_unknown", false)) {
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
                        setObjectField(param.thisObject, "SCAN_TIMEOUT_TIME", 0);
                    }
                });

                findAndHookConstructor("com.meizu.safe.security.bean.MzStoreAppInfo", lpparam.classLoader, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        MzStoreAppInfo = param;
                        // confirm button
                        findAndHookMethod("com.android.packageinstaller.PackageInstallerActivity", "onScanFinish", new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                setObjectField(MzStoreAppInfo, "showConfirm", getBool("mzInstaller_confirm", true));
                            }
                        });
                    }
                });
            }

            // virus
            if (getBool("mzInstaller_virus", false)) {
                findAndHookMethod("com.android.packageinstaller.PackageInstallerActivity", "startSafeCheckService", String.class, XC_MethodReplacement.returnConstant(null));
            }
        }
    }

    private boolean isLoadPackage(String packageName) {
        return (lpparam.packageName.equals(packageName));
    }

    private Object getField(Object object, String name) {
        try {
            return XposedHelpers.findField(object.getClass(), name).get(object);
        } catch (Throwable t) {
            return null;
        }
    }

    private void setObjectField(Object object, String fieldName, Object value) {
        try {
            XposedHelpers.findField(object.getClass(), fieldName).set(object, value);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private boolean getBool(String key, boolean defValue) {
        return XPrefs.getPrefs().getBoolean(key, defValue);
    }
}
