package com.dut.note.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.blogspot.sontx.libex.DateTime;
import com.dut.note.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

public final class FileUtil {
    public static final String mLocalFolder;

    static {
        Context context = App.getAppContext();
        PackageManager m = context.getPackageManager();
        String path = context.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(path, 0);
            path = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("UTIL", "Error Package name not found ", e);
        }
        mLocalFolder = path;
    }

    public static String getRandomFileName() {
        Random random = new Random(DateTime.tick());
        StringBuilder builder = new StringBuilder(16);
        for (int i = 0; i < builder.capacity(); i++) {
            builder.append((int) (random.nextFloat() * 10));
        }
        return builder.toString();
    }

    public static String combine(String path, String fileName) {
        path = path.replace("\\", "/");
        if (!path.endsWith("/"))
            path = path + "/";
        return path + fileName;
    }

    public static boolean writeAllBytes(FileInputStream inputStream, FileOutputStream outputStream) {
        byte[] buff = new byte[0x800];
        int ret = 0;
        try {
            do {
                ret = inputStream.read(buff);
                if (ret > 0)
                    outputStream.write(buff, 0, ret);
                else
                    break;
            } while (true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] readAllBytes(InputStream inputStream) {
        if(inputStream == null)
            return null;
        byte[] buff;
        try {
            buff = new byte[inputStream.available()];
            inputStream.read(buff);
            return buff;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isLocalFile(String fileName) {
        int count = 0;
        for (int i = 0; i < fileName.length(); i++) {
            if (fileName.charAt(i) == '/')
                count++;
        }
        return count > 0;
    }

    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }
}
