package com.cj.jplugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.cj.jplugin.bundle.BundleParser;
import com.cj.jplugin.hook.HookActivityProxy;
import com.cj.jplugin.plugin.PluginInfo;
import com.cj.jplugin.utils.ReflectUtil;
import com.cj.jplugin.utils.Util;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * ###只用于学习交流###
 * 学习前需要了解类加载器、反射及动态代理
 * 技术方案：
 * 1.宿主apk和插件apk都是使用PathClassLoader加载
 * 2.宿主apk资源和插件apk资源是隔离的
 * 3.Hook IActivityManager.startActivity和ActivityThread.mH.mCallback来骗过AMS对Activity的检测
 * 支持范围：
 * 1.四大组件只支持Activity
 * 2.Activity具有生命周期
 * 3.Activity的launchMode只支持standard
 * blog：
 * https://blog.csdn.net/cj_286/article/details/103569514
 */
public class PluginManager {
    private volatile static PluginManager mInstance;

    private static Context mContext;
    private static File mOptFile;
    private static Map<String, PluginInfo> mPluginMap;
    private static boolean sIs64bit = false;

    private PluginManager() {
        mPluginMap = new ConcurrentHashMap<>();
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context){
        mContext = context;
        mOptFile = mContext.getDir("opt", mContext.MODE_PRIVATE);
        sIs64bit = context.getApplicationInfo().nativeLibraryDir.contains("64");
    }

    /**
     * 绕过AMS检测Activity
     * @param proxyClass 占坑的Activity
     */
    public void hookActivity(Class proxyClass) {
        HookActivityProxy proxy = new HookActivityProxy(mContext,proxyClass);
        proxy.hookStartActivity();
        proxy.hookLaunchActivity();
    }

    /**
     * 获取单例对象方法
     * @return
     */
    public static PluginManager getInstance() {
        if (mInstance == null) {
            synchronized (PluginManager.class) {
                if (mInstance == null) {
                    mInstance = new PluginManager();
                }
            }
        }
        return mInstance;
    }


    public PluginInfo loadApk(String apkPath) {

        if (mPluginMap.get(apkPath) != null) {
            return mPluginMap.get(apkPath);
        }

        PluginInfo pluginInfo = new PluginInfo();
        DexClassLoader pluginDexClassLoader = createPluginDexClassLoader(apkPath);
        pluginInfo.setClassLoader(pluginDexClassLoader);
        //AssetManager pluginAssetManager = createPluginAssetManager(apkPath);//使用了反射@hide
        Resources pluginResources = Util.createPluginResources(mContext, apkPath);//无反射，无Hook
        AssetManager pluginAssetManager = pluginResources != null ? pluginResources.getAssets() : null;
        pluginInfo.setAssetManager(pluginAssetManager);
        //Resources pluginResources = createPluginResources(apkPath);//使用了反射@hide
        pluginInfo.setResources(pluginResources);
        pluginInfo.setApkPath(apkPath);

        String apkPackageName = Util.getApkPackageName(mContext, apkPath);
        pluginInfo.setPackageName(apkPackageName);

        BundleParser bundleParser = BundleParser.parsePackage(mContext, new File(apkPath), apkPackageName);
        pluginInfo.setBundleParser(bundleParser);
        mPluginMap.put(apkPackageName, pluginInfo);

        ensureStringBlocks(pluginAssetManager);
        mergeDexElements(mContext,apkPath);

        return pluginInfo;
    }

    public PluginInfo getPluginInfo(String packageName){
        return mPluginMap.get(packageName);
    }

    public String getApkPath(String packageName){
        PluginInfo pluginInfo = getPluginInfo(packageName);
        if(pluginInfo != null){
            return pluginInfo.getApkPath();
        }
        return null;
    }

    /**
     * 为插件apk创建对应的classLoader,暂时用不到
     */
    private static DexClassLoader createPluginDexClassLoader(String apkPath) {

        DexClassLoader classLoader = new DexClassLoader(apkPath,
                mOptFile.getAbsolutePath(), null, null);
        return classLoader;
    }

    /**
     * 为对应的插件创建AssetManager
     */
    private static AssetManager createPluginAssetManager(String apkPath) {

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
                    String.class);
            addAssetPath.invoke(assetManager, apkPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 为对应的插件创建resources
     * @param apkPath
     * @return
     */
    private static Resources createPluginResources(String apkPath) {

        AssetManager assetManager = createPluginAssetManager(apkPath);
        if(assetManager != null){
            Resources superResources = mContext.getResources();
            Resources pluginResources = new Resources(assetManager,
                    superResources.getDisplayMetrics(), superResources.getConfiguration());
            return pluginResources;
        }
        return null;
    }

    private void ensureStringBlocks(Object target){
        try {
            ReflectUtil.invoke(AssetManager.class,target,"ensureStringBlocks",new Class[]{},new Object[]{});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将插件中的Element[]合并到PathClassLoader中的Element[]
     * 不管是宿主中的类还是插件中的类都由PathClassLoader加载
     * @param context
     * @param apkPath
     */
    private static void mergeDexElements(Context context,String apkPath){
        try {
            //获取PathClassLoader
            ClassLoader pathClassLoaderClass = context.getClassLoader();
            //获取PathClassLoader(BaseDexClassLoader)的DexPathList对象变量pathList
            Object pathList = ReflectUtil.getFieldValue(BaseDexClassLoader.class, pathClassLoaderClass, "pathList");
            //获取DexPathList的Element[]对象变量dexElements
            Object[] dexElements = (Object[]) ReflectUtil.getFieldValue(pathList.getClass(),pathList,"dexElements");
            //获得Element类型
            Class<?> dexElementsType = dexElements.getClass().getComponentType();
            //创建一个新的Element[], 将用于替换原始的数组
            Object[] newdexElementList = (Object[]) Array.newInstance(dexElementsType, dexElements.length + 1);
            //构造插件的Element
            File apkFile = new File(apkPath);
            File optDexFile = new File(apkPath.replace(".apk", ".dex"));
            Class[] parameterTypes = {DexFile.class,File.class};
            Object[] initargs = {DexFile.loadDex(apkPath, optDexFile.getAbsolutePath(), 0),apkFile};
            Object pluginDexElements = ReflectUtil.newInstance(dexElementsType, parameterTypes, initargs);
            Object[] pluginDexElementsList = new Object[]{pluginDexElements};
            //把原来PathClassLoader中的elements复制进去新的Element[]中
            System.arraycopy(dexElements, 0, newdexElementList, 0, dexElements.length);
            //把插件的element复制进去新的Element[]中
            System.arraycopy(pluginDexElementsList, 0, newdexElementList, dexElements.length, pluginDexElementsList.length);
            //替换原来PathClassLoader中的dexElements值
            ReflectUtil.setFieldValue(pathList.getClass(),pathList,"dexElements",newdexElementList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean is64bit() {
        return sIs64bit;
    }

    /**
     * 启动插件apk中的Launcher Activity
     * 参考了和使用了Small框架的代码
     * @param packageName
     */
    public void startLauncherActivity(String packageName){
        PluginInfo pluginInfo = getPluginInfo(packageName);
        if(pluginInfo != null){
            BundleParser bundleParser = pluginInfo.getBundleParser();
            if(bundleParser != null){
                boolean b = bundleParser.collectActivities();
                String defaultActivityName = bundleParser.getDefaultActivityName();
                if(b && defaultActivityName != null){
                    Util.startActivity(mContext,packageName,defaultActivityName);
                }
            }
        }
    }
}
