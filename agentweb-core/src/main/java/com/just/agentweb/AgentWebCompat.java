package com.just.agentweb;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.smtt.sdk.WebView;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cenxiaozhong
 * @date 2021/11/24
 * @since 1.0.0
 */
public class AgentWebCompat {


    public static void initX5(Context context){
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_PRIVATE_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setDownloadWithoutWifi(true);
        Log.d("AgentWebCompat","内核版本："+QbSdk.getTbsVersion(context));

        if (QbSdk.getTbsVersion(context) == 0) {
            Log.d("AgentWebCompat","非X5内核，加载本地内核");
            File copy = new File(FileUtils.getCacheDir(context)+"/tbs/tbs_core_046238_20230210164344_nolog_fs_obfs_armeabi_release.apk");
            AssetsUtil.putAssetsToSDCard(context, "tbs",FileUtils.getCacheDir(context));
            Log.d("AgentWebCompat","copy:"+copy.getAbsolutePath());
//            QbSdk.installLocalQbApk(context,"44286",copy.getAbsolutePath(),null);
            QbSdk.installLocalTbsCore(context, 44286, copy.getAbsolutePath());
        }
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核
                Log.d("AgentWebCompat", "onCoreInitFinished");
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             *
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {
                Log.d("AgentWebCompat", "onViewInitFinished：" + isX5);
            }
        });
    }

    /**
     * 来之 https://github.com/Justson/AgentWeb/issues/934 建议
     * https://juejin.cn/post/6950091477192015902
     * fix Using WebView from more than one process
     * @param context
     */
    public static void setDataDirectorySuffix(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        try {

            Set<String> pathSet = new HashSet<>();
            String suffix = "";
            String dataPath = context.getDataDir().getAbsolutePath();
            String webViewDir = "/app_webview";
            String huaweiWebViewDir = "/app_hws_webview";
            String lockFile = "/webview_data.lock";
            String processName = ProcessUtils.getCurrentProcessName(context);
            if (!TextUtils.equals(context.getPackageName(), processName)) {//判断不等于默认进程名称
                suffix = TextUtils.isEmpty(processName) ? context.getPackageName() : processName;
                WebView.setDataDirectorySuffix(suffix);
                suffix = "_" + suffix;
                pathSet.add(dataPath + webViewDir + suffix + lockFile);
                if (RomUtils.isHuawei()) {
                    pathSet.add(dataPath + huaweiWebViewDir + suffix + lockFile);
                }
            }else{
                //主进程
                suffix = "_" + processName;
                pathSet.add(dataPath + webViewDir + lockFile);//默认未添加进程名后缀
                pathSet.add(dataPath + webViewDir + suffix + lockFile);//系统自动添加了进程名后缀
                if (RomUtils.isHuawei()) {//部分华为手机更改了webview目录名
                    pathSet.add(dataPath + huaweiWebViewDir + lockFile);
                    pathSet.add(dataPath + huaweiWebViewDir + suffix + lockFile);
                }
            }
            for (String path : pathSet) {
                File file = new File(path);
                if (file.exists()) {
                    tryLockOrRecreateFile(file);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private static void tryLockOrRecreateFile(File file) {
        try {
            FileLock tryLock = new RandomAccessFile(file, "rw").getChannel().tryLock();
            if (tryLock != null) {
                tryLock.close();
            } else {
                createFile(file, file.delete());
            }
        } catch (Exception e) {
            e.printStackTrace();
            boolean deleted = false;
            if (file.exists()) {
                deleted = file.delete();
            }
            createFile(file, deleted);
        }
    }

    private static void createFile(File file, boolean deleted){
        try {
            if (deleted && !file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

