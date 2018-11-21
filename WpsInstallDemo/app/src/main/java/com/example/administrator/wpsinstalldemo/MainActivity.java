package com.example.administrator.wpsinstalldemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String downloadUrl = "http://wxz.myapp.com/16891/31B86FE1AF6605F0A29F16255D0EDE0B.apk?fsname=cn.wps.moffice_eng_11.3.5_340.apk&hsr=4d5s";//wps下载地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestAllPower();
        }
        String path = Environment.getExternalStorageDirectory() + "/DownloadFile/test.doc";
        if(isInstalled(IntentUtils.getAttachmentIntent(new File(path),"doc"))){
            startActivity(IntentUtils.getAttachmentIntent(new File(path),"doc"));
        }else{
            Toast.makeText(this,"应用未安装",Toast.LENGTH_SHORT).show();
            installWpsPop();
        }
    }
    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    private boolean isInstalled(Intent intent){
        PackageManager pManager = this.getPackageManager();
        @SuppressLint("WrongConstant") List list = pManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        if(list.size()>0){
            return true;
        }else{
            return false;
        }
    }
    public void installWpsPop(){
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示");
        builder.setMessage("点击确定下载WPS!");
        builder.setCancelable(false); // 将对话框设置为不可取消
        // 给按钮添加注册监听
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 点击按钮所调用的方法
                installWps();
            }
        });
        builder.setNeutralButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 点击按钮所调用的方法
            }
        });
        builder.show();
    }

    private void installWps() {
        File file = new File(UpdateServices.save_url);
        if (!file.exists()) {
            file.mkdir();
        }
        File fileApk = new File(UpdateServices.save_url + "/wps.apk");
        if (fileApk.exists()) {
            Log.e("路径存在", fileApk.toString());
            Log.e("343434", "------------");
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(fileApk);
            if (Build.VERSION.SDK_INT >= 24) {
               uri = FileProvider.getUriForFile(this, getPackageName()+".file_provider", fileApk);
                intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent2.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent2);
            fileApk.delete();
            Log.e("11111","111111");
        } else {
            if (Build.VERSION.SDK_INT >= 24) {
                Log.e("242424", "------------");
                Intent intent = new Intent(MainActivity.this,
                        UpdateServices.class);
                intent.putExtra("Key_App_Name", "WPS");
                intent.putExtra("Key_Down_Url", downloadUrl);
                startService(intent);
            } else {
                Log.e("444444", "------------");
                Intent intent = new Intent(MainActivity.this,
                        UpdateServices.class);
                intent.putExtra("Key_App_Name", "WPS");
                intent.putExtra("Key_Down_Url", downloadUrl);
                startService(intent);
            }
        }
    }
}
