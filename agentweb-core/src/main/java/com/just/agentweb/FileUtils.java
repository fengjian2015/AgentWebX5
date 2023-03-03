package com.just.agentweb;

import android.content.Context;
import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Time：2023/3/3
 * Author：feng
 * Description：
 */
public class FileUtils {

    /** 复制文件，可以选择是否删除源文件 */
    public static boolean copyFile(File srcFile, File destFile, boolean deleteSrc) {
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
                out.flush();
            }
            if (deleteSrc) {
                srcFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(out);
            close(in);
        }
        return true;
    }

    /** 关闭流 */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * 获取程序下载文件夹
     *
     * @param context
     * @return
     */
    public static String getCacheDir(Context context) {
        String path = null;
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            File f = context.getExternalFilesDir("");
            if (f == null) {
                path = context.getFilesDir().getAbsolutePath();
            } else {
                path = f.getAbsolutePath();
            }
        } else {
            path = context.getFilesDir().getAbsolutePath();
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
