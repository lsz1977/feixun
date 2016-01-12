package com.cchat.utils;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class MyIntent

{
    public static Intent getPictureIntent(String path) {
        //getUnknowIntent(  path, filename );
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(path);
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        return intent;
    }

    public static Intent getAudioIntent(String path) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(path);
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        return intent;
    }

    public static Intent getVideoIntent(String path) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(path);
        intent.setDataAndType(Uri.fromFile(file), "video/*");
        return intent;
    }

    public static Intent getUnknowIntent(String path) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(path);
        intent.setDataAndType(Uri.fromFile(file), "*/*");
        return intent;
    }

    public static Intent getTextIntent(String path) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(path);
        intent.setDataAndType(Uri.fromFile(file), "text/*");
        return intent;
    }

    //文件类型代码
    public static final int UnknowType = 0;//未知类型
    public static final int PictureType = 1;//代表图片类型
    public static final int VideoType = 2;//代表视频
    public static final int AudioType = 3;//代表音频
    public static final int TextType = 4;//代表文档

    /*int getFileTyte(String filename)
     * 功能:静态函数，根据文件名字返回文件类型
     * 参数：可以是文件名字，当然也可以是文件相对或者绝对路径
     * 返回值：表示文件类型的代码,代码含义参见本文件中的静态变量
     * */
    public static int getFileTyte(String filename) {
        if (filename.endsWith(".gif") || filename.endsWith(".jpg") || filename.endsWith(".png")
                || filename.endsWith(".GIF") || filename.endsWith(".JPG") || filename.endsWith(".PNG"))
            return PictureType;
        if (filename.endsWith(".mp3"))
            return AudioType;
        if (filename.endsWith(".mp4") || filename.endsWith(".rmvb") || filename.endsWith(".rm"))
            return VideoType;
        if (filename.endsWith(".txt") || filename.endsWith(".html"))
            return TextType;
        return UnknowType;
    }
}