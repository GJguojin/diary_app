package com.gj.diary.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2017/6/24.
 */
public class PropertiesUtil {

    private static Properties props;

    private static final String salt = "diary";

    private final static String CONFIG_NAME="diaryConfig.properties";

    /**
     * 得到属性值
     *
     * @param keyName
     * @return String
     * @author jin.guo 2016年5月30日
     */
    public static String getProperties(Context context, String keyName) {
        String strValue = null;
        if (props == null) {
            props = new Properties();
            File filesDir = context.getFilesDir();
            if (!filesDir.exists()) {
                filesDir.mkdirs();
            }
            filesDir = new File(filesDir, "diaryConfig.properties");
            if (!filesDir.exists()) {
                try {
                    filesDir.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = context.openFileInput(CONFIG_NAME);
                props.load(context.openFileInput(CONFIG_NAME));
            } catch (FileNotFoundException e) {
                Log.e("PropertiesUtil", "config.properties Not Found Exception", e);
                return null;
            } catch (IOException e) {
                Log.e("PropertiesUtil", "config.properties IO Exception", e);
                return null;
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        strValue = props.getProperty(keyName);
        return strValue;
    }

    /**
     * 保存属性
     *
     * @param context
     * @param keyName
     * @param keyValue
     */
    public static void saveProperties(Context context, String keyName, String keyValue) {
        FileInputStream fileInputStream = null;
        OutputStream out = null;
        try {
            if (props == null) {
                props = new Properties();
                fileInputStream = context.openFileInput(CONFIG_NAME);
                props.load(fileInputStream);
            }
            out = context.openFileOutput(CONFIG_NAME, Context.MODE_PRIVATE);
            if (!props.contains("password")) {
                props.setProperty("password", MD5Util.getMd532("00000000" + salt));
            }
            if (!props.contains("passwordPhoto")) {
                props.setProperty("passwordPhoto", MD5Util.getMd532("00000000" + salt));
            }
            if ("password".equals(keyName)) {
                props.setProperty("password", MD5Util.getMd532(keyValue + salt));
            } else if ("passwordPhoto".equals(keyName)) {
                props.setProperty("passwordPhoto", MD5Util.getMd532(keyValue + salt));
            } else {
                props.setProperty(keyName, keyValue);
            }
            props.store(out, null);
        } catch (FileNotFoundException e) {
            Log.e("PropertiesUtil", "config.properties Not Found Exception", e);
        } catch (IOException e) {
            Log.e("PropertiesUtil", "config.properties IO Exception", e);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
