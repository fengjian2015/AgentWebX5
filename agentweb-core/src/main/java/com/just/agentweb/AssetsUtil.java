package com.just.agentweb;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.SyncStateContract;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Time：2023/3/3
 * Author：feng
 * Description：
 */
public class AssetsUtil {
    /**
     * 获取assets目录下的图片
     * @param context 上下文
     * @param fileName  文件名
     * @return  Bitmap图片
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName){
        Bitmap bitmap = null;
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 获取assets目录下的单个文件
     * 这种方式只能用于webview加载
     * 读取文件夹，直接取路径是不行的
     * @param context 上下文
     * @param fileName  文件夹名
     * @return File
     */
    public static File getFileFromAssetsFile(String fileName){
        String path = "file:///android_asset/" + fileName;
        File file = new File(path);
        return file;
    }

    /**
     * 获取assets目录下所有文件
     * @param context  上下文
     * @param path  文件地址
     * @return files[] 文件列表
     */
    public static String[] getFilesFromAssets(Context context, String path){
        AssetManager assetManager = context.getAssets();
        String files[] = null;
        try {
            files = assetManager.list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    /**
     * 将assets下的文件放到sd指定目录下
     * @param context     上下文
     * @param assetsPath  assets下的路径
     */
    public static void putAssetsToSDCard(Context context, String assetsPath){
        putAssetsToSDCard(context, assetsPath, context.getExternalFilesDir(null).getAbsolutePath());
    }

    /**
     * 将assets下的文件放到sd指定目录下
     * @param context      上下文
     * @param assetsPath   assets下的路径
     * @param sdCardPath   sd卡的路径
     */
    public static void putAssetsToSDCard(Context context, String assetsPath, String sdCardPath){
        AssetManager assetManager = context.getAssets();
        try {
            String files[] = assetManager.list(assetsPath);
            if (files.length == 0) {
                // 说明assetsPath为空,或者assetsPath是一个文件
                InputStream is = assetManager.open(assetsPath);
                byte[] mByte = new byte[1024];
                int bt = 0;
                File file = new File(sdCardPath + File.separator
                        + assetsPath.substring(assetsPath.lastIndexOf('/')));
                if (!file.exists()) {
                    // 创建文件
                    file.createNewFile();
                } else {
                    //已经存在直接退出
                    return;
                }

                // 写入流
                FileOutputStream fos = new FileOutputStream(file);
                // assets为文件,从文件中读取流
                while ((bt = is.read(mByte)) != -1) {
                    // 写入流到文件中
                    fos.write(mByte, 0, bt);
                }

                // 刷新缓冲区
                fos.flush();
                // 关闭读取流
                is.close();
                // 关闭写入流
                fos.close();
            } else {
                // 当mString长度大于0,说明其为文件夹
                sdCardPath = sdCardPath + File.separator + assetsPath;
                File file = new File(sdCardPath);
                if (!file.exists()) {
                    // 在sd下创建目录
                    file.mkdirs();
                }

                // 进行递归
                for (String stringFile : files) {
                    putAssetsToSDCard(context, assetsPath + File.separator
                            + stringFile, sdCardPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
