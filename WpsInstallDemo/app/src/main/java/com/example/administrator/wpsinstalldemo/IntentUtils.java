package com.example.administrator.wpsinstalldemo;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by Administrator on 2018/11/21.
 */

public class IntentUtils {
    public static Intent getAttachmentIntent(File file, String filetype){
        filetype = filetype.toLowerCase();
        if(filetype.equals("doc")||filetype.equals("docx")){
            return getWordFileIntent(file) ;
        }
        else if(filetype.equals("pdf")){
            return getPdfFileIntent(file) ;
        }
        else if(filetype.equals("image")){
            return getImageFileIntent(file) ;
        }
        else if(filetype.equals("jpg")||filetype.equals("gif")||filetype.equals("png")||
                filetype.equals("jpeg")||filetype.equals("bmp")){
            return getImageFileIntent(file) ;
        }
        else if(filetype.equals("txt")){
            return getTextFileIntent(file) ;
        }
        else if(filetype.equals("xls")||filetype.equals("xlsx")){
            return getExcelFileIntent(file) ;
        }
        else if(filetype.equals("ppt")||filetype.equals("pptx")){
            return getPptFileIntent(file) ;
        }
        else if(filetype.equals("html")||filetype.equals("htm")){
            return getHtmlFileIntent(file) ;
        }
        return getDefaultFileIntent(file);

    }

    public static Intent getWordFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    public static Intent getHtmlFileIntent(String content) {
        Uri uri = Uri.parse(content).buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content").encodedPath(content).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    public static Intent getTextFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri2 = Uri.fromFile(file);
        intent.setDataAndType(uri2, "text/plain");
        return intent;
    }

    public static Intent getImageFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }


    //Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
    public static Intent getPdfFileIntent(File file) {
        Uri path = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
    //Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent( File file  ){
        Uri path = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(path, "application/vnd.ms-excel");
        return intent;
    }
    //Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(File file ){
        Uri path = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(path, "application/vnd.ms-powerpoint");
        return intent;
    }
    private static Intent getHtmlFileIntent(File file) {

        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }
    public static Intent getDefaultFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri2 = Uri.fromFile(file);
        intent.setDataAndType(uri2, "");
        return intent;
    }
}
