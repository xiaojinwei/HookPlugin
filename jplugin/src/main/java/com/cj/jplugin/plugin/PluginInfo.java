package com.cj.jplugin.plugin;

import android.content.res.AssetManager;
import android.content.res.Resources;

import com.cj.jplugin.bundle.BundleParser;

import dalvik.system.DexClassLoader;

public class PluginInfo {

    private DexClassLoader mClassLoader;

    private AssetManager mAssetManager;

    private Resources mResources;

    private String packageName;

    private String apkPath;

    private BundleParser bundleParser;

    public BundleParser getBundleParser() {
        return bundleParser;
    }

    public void setBundleParser(BundleParser bundleParser) {
        this.bundleParser = bundleParser;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public DexClassLoader getClassLoader() {
        return mClassLoader;
    }

    public void setClassLoader(DexClassLoader classLoader) {
        this.mClassLoader = classLoader;
    }

    public AssetManager getAssetManager() {
        return mAssetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.mAssetManager = assetManager;
    }

    public Resources getResources() {
        return mResources;
    }

    public void setResources(Resources resources) {
        this.mResources = resources;
    }

}
