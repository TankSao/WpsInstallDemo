package com.example.administrator.wpsinstalldemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateServices extends Service {
    public UpdateServices() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private TextView tv;
    private String app_name;// 文件名
    private String down_url;// 下载文件地址
    public static String save_url = Environment.getExternalStorageDirectory().getPath() + "/wpsDownload";

    private static final int TIMEOUT = 10 * 1000;// 超时
    private static final int DOWN_OK = 1;
    private static final int DOWN_ERROR = 0;

    // 通知
    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews contentView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        app_name = intent.getStringExtra("Key_App_Name");
        down_url = intent.getStringExtra("Key_Down_Url");

        Log.e("app_name",app_name);
        Log.e("down_url",down_url);
        if (util.createFile(save_url, app_name)) {
            createNotification();
            // 线程开启
            new DownLoadThread().start();
        } else {
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void createNotification() {
        notification = new Notification(R.mipmap.wps, app_name + "正在下载",
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        // 自定义 Notification 的显示
        contentView = new RemoteViews(getPackageName(), R.layout.test_notification_item);
        contentView.setTextViewText(R.id.notificationTitle, app_name + "正在下载");
        contentView.setTextViewText(R.id.notificationPercent, "0%");
        contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
        notification.contentView = contentView;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.layout.test_notification_item, notification);
    }

    private class DownLoadThread extends Thread {

        @Override
        public void run() {
            Message message = new Message();
            try {
                long downloadSize = downloadUpdateFile(down_url, util.updateFile.toString());
                if (downloadSize > 0) {
                    message.what = DOWN_OK;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = DOWN_ERROR;
                handler.sendMessage(message);
            }
        }
    }

    public long downloadUpdateFile(String down_url, String file) throws Exception {
        int totalSize; // 文件总大小
        int downloadCount = 0; // 已经下载好的大小
        int updateCount = 0; // 已经上传的文件大小
        int downCount = 1;// 已下载好的百分比

        InputStream inputStream;
        OutputStream outputStream;

        URL url = new URL(down_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        url.openConnection();
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        // 获取下载文件的总管大小size
        totalSize = httpURLConnection.getContentLength();

        if (httpURLConnection.getResponseCode() == 404) {
            // 这个地方应该加一个下载失败的处理，但是，因为我们在外面加了一个try---catch，已经处理了Exception,所以不用处理
            throw new Exception("fail!");
        }

        inputStream = httpURLConnection.getInputStream();
        outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉

        byte buffer[] = new byte[1024];
        int readsize = 0;

        while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
            downloadCount += readsize;
            int num = downloadCount * 100 / totalSize;
            if (num > downCount || totalSize == downloadCount) {
                downCount = downloadCount * 100 / totalSize;
                // 改变通知栏
                contentView.setTextViewText(R.id.notificationPercent, downCount + "%");
                contentView.setProgressBar(R.id.notificationProgress, 100, downCount, false);
                notification.contentView = contentView;
                notificationManager.notify(R.layout.test_notification_item, notification);
            }
        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        outputStream.close();
        return downloadCount;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_OK:
                    // 改变通知栏
                    contentView.setTextViewText(R.id.notificationTitle, app_name + "下载完成");
                    notification.contentView = contentView;
                    notificationManager.notify(R.layout.test_notification_item, notification);
                    //安装
                    Uri uri = Uri.fromFile(util.updateFile);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    // 调用系统自带安装环境
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri contentUri = FileProvider.getUriForFile(UpdateServices.this, getPackageName() + ".file_provider", util.updateFile);
                        install.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        install.setDataAndType(uri, "application/vnd.android.package-archive");
                    }
                    getApplicationContext().startActivity(install);
                    stopSelf();
                    notificationManager.cancelAll();
                    break;
                case DOWN_ERROR:
                    // 改变通知栏
                    util.delFile(save_url + "/" + app_name + ".apk");
                    contentView.setTextViewText(R.id.notificationTitle, app_name + "下载失败");
                    notification.contentView = contentView;
                    notificationManager.notify(R.layout.test_notification_item, notification);
                    stopSelf();
                    break;
                default:
                    break;
            }
        }
    };

}
