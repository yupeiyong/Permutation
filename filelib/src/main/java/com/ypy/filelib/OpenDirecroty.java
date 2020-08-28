package com.ypy.filelib;

import android.content.Context;
import android.content.Intent;

import java.io.File;

public class OpenDirecroty {
    /**
     * Android传入type打开文件
     * 未方法未能实现打开指定文件夹的目的
     * @param mContext
     * @param file
     * @param type
     */
    public static void open(Context mContext, File file, String type) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        FileProviderUtils.setIntentDataAndType(mContext, intent, type, file, true);
        mContext.startActivity(intent);
    }
}
