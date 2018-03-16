package com.gj.diary.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/24.
 */
public class PropertiesUtil {

    public static final String PASSWORD_DIARY="password";
    public static final String PASSWORD_PHOTO="passwordPhoto";

    public static final Map<String,String> PROPERTIES  = new HashMap<>();
    public static final Map<String,String> PROPERTIES_NAME  = new HashMap<>();

    private static Properties props;

    public static String diaryPassword;


    private final static String CONFIG_NAME="diaryConfig.properties";

    static {
        PROPERTIES.put("photo_radito","1.7808");
        PROPERTIES_NAME.put("photo_radito","图片宽高比");

        PROPERTIES.put("create_title","日记制作");
        PROPERTIES_NAME.put("create_title","创建页标题");

        PROPERTIES.put("query_title","日记浏览");
        PROPERTIES_NAME.put("query_title","浏览页标题");

        PROPERTIES.put("diary_text_title","2050年的自己：");
        PROPERTIES_NAME.put("diary_text_title","正文标题");

        PROPERTIES.put("diary_text_start","这张照片还记得吗？这一天是[date],");
        PROPERTIES_NAME.put("diary_text_start","正文开始");

        PROPERTIES.put("use_storage","true");
        PROPERTIES_NAME.put("use_storage","使用存储");

        PROPERTIES.put("default_background","/diary/defaultBitmap.jpg");
        PROPERTIES_NAME.put("default_background","默认背景");

        PROPERTIES.put("file_path","/diary/picture/");
        PROPERTIES_NAME.put("file_path","图片路径");

        PROPERTIES.put("storage_path","/diary/storage/");
        PROPERTIES_NAME.put("storage_path","存储路径");

    }

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
            filesDir = new File(filesDir, CONFIG_NAME);
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
                props.load(fileInputStream);
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
            if (props.getProperty(PASSWORD_DIARY) == null || "".equals(props.getProperty(PASSWORD_DIARY) )) {
                props.setProperty(PASSWORD_DIARY, MD5Util.getMd532("00000000" + ImageUtil.salt));
            }
            if (props.getProperty(PASSWORD_PHOTO)==null || "".equals(props.getProperty(PASSWORD_PHOTO))) {
                props.setProperty(PASSWORD_PHOTO, MD5Util.getMd532("00000000" + ImageUtil.salt));
            }
            if (PASSWORD_DIARY.equals(keyName)) {
                props.setProperty(PASSWORD_DIARY, MD5Util.getMd532(keyValue + ImageUtil.salt));
            } else if (PASSWORD_PHOTO.equals(keyName)) {
                props.setProperty(PASSWORD_PHOTO, MD5Util.getMd532(keyValue + ImageUtil.salt));
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

    public static boolean checkPassword(Context context,String password,String model){
        if(password != "" && password != null){
            String oldPassword = "";
            if("1".equals( model )){
                oldPassword = getProperties( context, PASSWORD_DIARY);
            }else{
                oldPassword = getProperties(context, PASSWORD_PHOTO);
            }
            if(getMd5String( password).equalsIgnoreCase( oldPassword )){
                if("1".equals( model )){
                    diaryPassword = password;
                }
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    public static String getMd5String(String password){
        return MD5Util.getMd532( password+ImageUtil.salt );
    }
}
