package com.cj.jplugin.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.cj.jplugin.bundle.BundleParser;

import java.io.File;

public class Util {

    public static PackageInfo getApkInfo(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        return info;
    }

    public static String getApkPackageName(Context context,String apkPath){
        PackageInfo apkInfo = getApkInfo(context, apkPath);
        if(apkInfo != null){
            return apkInfo.packageName;
        }
        return null;
    }

    /**
     * 构造插件Resources
     * @param context
     * @param apkPath
     * @return
     */
    public static Resources createPluginResources(Context context, String apkPath){
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);
        if (info == null || info.applicationInfo == null) {
            return null;
        }
        info.applicationInfo.sourceDir = apkPath;
        info.applicationInfo.publicSourceDir = apkPath;

        if (TextUtils.isEmpty(info.applicationInfo.processName)) {
            info.applicationInfo.processName = info.applicationInfo.packageName;
        }
        try {
             return pm.getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void startLauncherActivity(Context context,String apkPath){
        String apkPackageName = getApkPackageName(context, apkPath);
        BundleParser bundleParser = BundleParser.parsePackage(context, new File(apkPath), apkPackageName);
        boolean b = bundleParser.collectActivities();
        if(b){
            String defaultActivityName = bundleParser.getDefaultActivityName();
            if(defaultActivityName != null){
                startActivity(context,apkPackageName,defaultActivityName);
            }
        }
    }

    public static void startActivity(Context context,String packageName, String clazzName){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName,clazzName));
        wrapIntent(intent);
        context.startActivity(intent);
    }

    private static Intent wrapIntent(Intent intent){
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

}
