package com.ypy.filelib;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

//打开文件的辅助类
public class FileProviderUtils {
    public static Uri getUriForFile(Context mContext, File file) {
        Uri fileUri = null;
        //版本是否大于24，高版本手机
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(mContext, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }
 
    public static Uri getUriForFile24(Context mContext, File file) {
        Uri fileUri = androidx.core.content.FileProvider.getUriForFile(mContext,
                mContext.getPackageName() + ".fileprovider",
                file);
        return fileUri;
    }
 
    public static void setIntentDataAndType(Context mContext,
                                            Intent intent,
                                            String type,
                                            File file,
                                            boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(mContext, file), type);
            //使用addFlags添加读写权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
}