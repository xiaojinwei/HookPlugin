package com.cj.jplugin.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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
