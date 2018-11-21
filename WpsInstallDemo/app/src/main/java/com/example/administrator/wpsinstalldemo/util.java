package com.example.administrator.wpsinstalldemo;

import java.io.File;

/**
 * Created by Administrator on 2017/4/12.
 */

public class util {
        public static File updateDir;
        public static File updateFile;

        public static boolean createFile(String url, String name) {
            // 判断SD卡可读可写
            if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
                    .getExternalStorageState())) {
                updateDir = new File(url + "/");
                updateFile = new File(url + "/" + name + ".apk");
                if (!updateDir.exists()) {
                    updateDir.mkdirs();
                }
                if (!updateFile.exists()) {
                    try {
                        updateFile.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        public static void delFile(String path) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
}
