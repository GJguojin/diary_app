package com.gj.diary.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.VideoView;

import com.gj.diary.R;
import com.gj.diary.utils.DialogUtils;
import com.gj.diary.utils.ImageSplitUtil;
import com.gj.diary.utils.ImageUtil;
import com.gj.diary.utils.PropertiesUtil;
import com.gj.diary.view.DiaryChangePassDialog;
import com.gj.diary.view.DiaryCreateDialog;
import com.gj.diary.view.DiaryImageView;
import com.gj.diary.view.DiaryLoginDialog;
import com.gj.diary.view.DiaryPropertyDialog;
import com.gj.diary.view.DiaryThemeDialog;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends DiaryBaseActivity implements View.OnClickListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static File rootDir;

    private static final String TAG = "MainActivity";
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String VIDEO_UNSPECIFIED = "video/*";
    public static String FILE_PATH = "/diary/picture/";
    public static String STORAGE_PATH = "/diary/storage/";
    public static String DF_BITMAP_PATH = "/diary/defaultBitmap.jpg";

    private String diaryStartText = "";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private static String dateString;


    private Button dirayCreateButton = null; //生成日志按钮
    private Button diaryQueryButton = null;

    private TextView diaryTextTitle = null;
    private TextView diaryTextStartText = null;

    private EditText diaryTextContent;

    private String picturePath;
    private DiaryImageView diaryPicture;
    private VideoView diaryVideo;
    private LinearLayout diaryVideoLayout;
    private MediaController mediaco;

    private Dialog loadDialog;

    private DiaryCreateDialog dialog;

    //定义Handler对象
    public Handler handler = new Handler() {
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //只要执行到这里就关闭对话框
            DialogUtils.closeDialog(loadDialog);
            if (dialog != null) {
                dialog.dismiss();
            }
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, "日记生成完成", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "日记已经存在", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "日记生成失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MainActivity.this, "请先选择照片", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(MainActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(MainActivity.this, "属相配置成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    static {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            rootDir = Environment.getExternalStorageDirectory();
        } else {
            rootDir = Environment.getRootDirectory();
        }
    }

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String appTheme = PropertiesUtil.getProperties(this, "appTheme");
        if (appTheme != null && !"".equals(appTheme)) {
            this.setTheme(Integer.parseInt(appTheme));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        //校验密码设置
        checkSalt();

        final List<String> storagePaths = getStoragePath(this);
        if (storagePaths.size() > 1) {
            String rootParh = storagePaths.get(1);
            if (rootDir != null && !rootDir.getAbsolutePath().equals(rootParh)) {
                rootDir = new File(rootParh);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.diary);
        actionBar.setDisplayShowHomeEnabled(true);
        final String createTitle = PropertiesUtil.getProperties(this, "create_title");
        if (createTitle != null && !"".equals(createTitle)) {
            actionBar.setTitle(createTitle);
        } else {
            actionBar.setTitle(PropertiesUtil.PROPERTIES.get("create_title"));
        }
        actionBar.show();

        //图片路径
        final String filePath = PropertiesUtil.getProperties(this, "file_path");
        if (filePath != null && !"".equals(filePath)) {
            FILE_PATH = filePath;
        } else {
            FILE_PATH = PropertiesUtil.PROPERTIES.get("file_path");
            PropertiesUtil.saveProperties(this, "filePath", FILE_PATH);
        }

        //存储路径
        final String storagePath = PropertiesUtil.getProperties(this, "storage_path");
        if (storagePath != null && !"".equals(storagePath)) {
            STORAGE_PATH = storagePath;
        } else {
            STORAGE_PATH = PropertiesUtil.PROPERTIES.get("storage_path");
            PropertiesUtil.saveProperties(this, "storage_path", STORAGE_PATH);
        }

        float fontSize = 15.0f;
        final String fontSizeString = PropertiesUtil.getProperties(this, "diary_text_font");
        if (fontSizeString != null && !"".equals(fontSizeString)) {
            fontSize = Float.parseFloat(fontSizeString);
        }else{
            fontSize =  Float.parseFloat(PropertiesUtil.PROPERTIES.get("diary_text_font"));
            PropertiesUtil.saveProperties(this, "diary_text_font", ""+fontSize);
        }

        //默认背景图片
        String defaultBitmapPath = PropertiesUtil.getProperties(this, "default_background");
        if (defaultBitmapPath != null && !"".equals(defaultBitmapPath)) {
            DF_BITMAP_PATH = defaultBitmapPath;
        } else {
            DF_BITMAP_PATH = PropertiesUtil.PROPERTIES.get("default_background");
            PropertiesUtil.saveProperties(this, "default_background", DF_BITMAP_PATH);
        }
        
        diaryTextTitle = (TextView) this.findViewById(R.id.diary_text_title);
        final String textTitle = PropertiesUtil.getProperties(this, "diary_text_title");
        if (textTitle != null && !"".equals(textTitle)) {
            diaryTextTitle.setText(textTitle);
        } else {
            diaryTextTitle.setText(PropertiesUtil.PROPERTIES.get("diary_text_title"));
        }
        diaryTextTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize+1);

        //设置日记时间
        diaryTextStartText = (TextView) this.findViewById(R.id.diary_text_start);
        diaryStartText = PropertiesUtil.getProperties(this, "diary_text_start");
        if (diaryStartText != null && !"".equals(diaryStartText)) {
            diaryStartText = "\t\t\t\t" + diaryStartText;
        } else {
            diaryStartText = "\t\t\t\t" + PropertiesUtil.PROPERTIES.get("diary_text_start");
        }
        diaryTextStartText.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);

        Date date = new Date();
        String format = sdf.format(date);
        dateString = sdf1.format(date);
        diaryTextStartText.setText(diaryStartText.replace("[date]", format));
        diaryTextStartText.setOnClickListener(this);

        diaryTextContent = (EditText) this.findViewById(R.id.diary_text_content);
        diaryTextContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);

        //生成日记方法
        dirayCreateButton = (Button) this.findViewById(R.id.diray_create);
        dirayCreateButton.setOnClickListener(this);

        //浏览日记方法
        diaryQueryButton = (Button) this.findViewById(R.id.diary_query);
        diaryQueryButton.setOnClickListener(this);

        diaryPicture = (DiaryImageView) findViewById(R.id.diary_picture);
        diaryVideo = (VideoView) findViewById(R.id.diary_video);
        diaryVideoLayout = (LinearLayout) findViewById(R.id.diary_video_layout);
        diaryVideoLayout.setVisibility(View.GONE);

        Bitmap defaultDiaryPicture = null;
        File defaultBitmapFile = new File(rootDir, DF_BITMAP_PATH);
        if (defaultBitmapFile.exists()) {
            defaultDiaryPicture = BitmapFactory.decodeFile(defaultBitmapFile.getPath());
        } else {
            defaultDiaryPicture = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        }
        defaultDiaryPicture = getRoundedCornerBitmap(defaultDiaryPicture, 70);
        diaryPicture.setImageBitmap(defaultDiaryPicture);
        final String photoRadito = PropertiesUtil.getProperties(this, "photo_radito");
        if (photoRadito != null && !"".equals(photoRadito)) {
            try {
                final Float aFloat = Float.valueOf(photoRadito);
                diaryPicture.setMRadito(aFloat);
            } catch (Exception e) {
            }
        }
        diaryPicture.setOnClickListener(this);
        diaryVideoLayout.setOnClickListener(this);

    }

    private void  checkSalt(){
        String saltValue = PropertiesUtil.getProperties(this, "salt_value");
        ImageUtil.salt = saltValue;
        if(saltValue == null || "".equals(saltValue)){
            final EditText et = new EditText(this);
            et.setHint("4到8位字符，仅设置一次，请记牢..");
            et.setGravity(Gravity.CENTER);
            final AlertDialog saltDialog = new AlertDialog.Builder(this).setTitle("请输入产品秘钥")
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(et)
                    .setPositiveButton("确定", null)
                    .show();
            saltDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String salt = et.getText().toString();
                    if(salt != null && !"".equals(salt) && salt.length() >= 4 && salt.length() <= 8){
                        ImageUtil.salt = salt;
                        PropertiesUtil.saveProperties(MainActivity.this,"salt_value",salt);
                        saltDialog.dismiss();
                        Toast.makeText(MainActivity.this, "产品秘钥设置成功！", Toast.LENGTH_LONG).show();;
                    }else{
                        Toast.makeText(MainActivity.this, "产品秘钥不能为空,且长度在4到8位！", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem item = menu.findItem(R.id.look_salt_value);
        //产品秘钥查看
        String querySslt = PropertiesUtil.getProperties(this, "query_salt");
        if (querySslt != null && !"".equals(querySslt)) {
            if("true".equals(querySslt)){
                item.setVisible(true);
            }
        }else{
            PropertiesUtil.saveProperties(this, "query_salt", PropertiesUtil.PROPERTIES.get("query_salt"));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chang_login_pass_menu:
                new DiaryChangePassDialog(this, "1");
                break;
            case R.id.chang_photo_pass_menu:
                new DiaryChangePassDialog(this, "2");
                break;
            case R.id.chang_photo_radito_menu:
                new DiaryPropertyDialog(this);
                break;
            case R.id.chang_theme_menu:
                new DiaryThemeDialog(this);
                break;
            case R.id.look_salt_value:
                DiaryLoginDialog diaryLoginDialog = new DiaryLoginDialog(this,"0");
                diaryLoginDialog.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        picturePath = null;
        if (data == null) {
            return;
        }
        picturePath = getRealFilePath(this, data.getData());
        switch (requestCode) {
            case 1:
                diaryPicture.setVisibility(View.VISIBLE);
                diaryVideoLayout.setVisibility(View.GONE);
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inSampleSize = 5;
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath, newOpts);
                if (bitmap == null) {
                    picturePath = null;
                    Toast.makeText(MainActivity.this, "加载图片失败！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, picturePath);
                bitmap = getRoundedCornerBitmap(bitmap, 25);
                diaryPicture.setImageBitmap(bitmap);
                super.onActivityResult(requestCode, resultCode, data);
                break;
            case 2:
                diaryPicture.setVisibility(View.GONE);
                diaryVideoLayout.setVisibility(View.VISIBLE);
                if (mediaco == null) {
                    mediaco = new MediaController(this);
                }
                File file = new File(picturePath);
                if (file.exists()) {
                    //VideoView与MediaController进行关联
                    diaryVideo.setVideoPath(file.getAbsolutePath());
                    diaryVideo.setMediaController(mediaco);
                    mediaco.setMediaPlayer(diaryVideo);
                    //让VideiView获取焦点
                    diaryVideo.requestFocus();
                }
                super.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.diary_query:
                Log.i(TAG, "日记查看");
                Intent diaryQueryIntent = new Intent();
                diaryQueryIntent.setClass(MainActivity.this, DiaryQueryActivity.class);
                //启动
                MainActivity.this.startActivity(diaryQueryIntent);
                break;
            case R.id.diray_create:
                dialog = new DiaryCreateDialog(this);
                dialog.setDiaryCreateOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int diaryCreateType = dialog.getDiaryCreateType();
                        dealDiaryCreateClick(diaryCreateType, dialog.getSplitWidth(), dialog.getSplitHeight());
                    }
                });
                break;
            case R.id.diary_text_start:
                Log.i(TAG, "修改日记时间");
                Calendar now = Calendar.getInstance();
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            dateString = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            Date date = sdf1.parse(dateString);
                            dateString = sdf1.format(date);
                            diaryTextStartText.setText(diaryStartText.replace("[date]", sdf.format(date)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.diary_picture:
            case R.id.diary_video_layout:
                boolean useStorageAble = false;
                final String useStorage = PropertiesUtil.getProperties(this, "use_storage");
                if (useStorage != null && !"".equals(useStorage)) {
                    if ("true".equals(useStorage)) {
                        useStorageAble = true;
                    }
                } else {
                    useStorageAble = Boolean.parseBoolean(PropertiesUtil.PROPERTIES.get("use_storage"));
                }
                if (useStorageAble) {
                    String[] items = {"选择图片", "选择视频"};
                    AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
                    listDialog.setTitle("选择类型：");
                    listDialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent openPicture = new Intent(Intent.ACTION_PICK, null);
                                openPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                                startActivityForResult(openPicture, 1);
                            } else {
                                Intent openVideo = new Intent(Intent.ACTION_PICK, null);
                                openVideo.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, VIDEO_UNSPECIFIED);
                                startActivityForResult(openVideo, 2);
                            }
                        }
                    });
                    listDialog.show();
                } else {
                    Intent openPicture = new Intent(Intent.ACTION_PICK, null);
                    openPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                    startActivityForResult(openPicture, 1);
                }
            default:
                break;
        }
    }


    // 菜单项被选择事件
    public boolean dealDiaryCreateClick(final int itemid, final int splitWidth, final int splitHeight) {
        Log.i(TAG, "日记生成开始.....");
        verifyStoragePermissions(this);
        if (picturePath == null) {
            handler.sendEmptyMessage(3);
            return false;
        }
        if (dateString == null || "".equals(dateString)) {
            dateString = sdf1.format(new Date());
        }
        String filePath = FILE_PATH;
        String fileName = dateString + ".jpg";
        if (itemid == R.id.diary_create_storage) { //文件存储
            filePath = STORAGE_PATH;
            fileName = new File(picturePath).getName();
        }
        filePath += dateString.split("-")[0] + "/" + dateString.split("-")[0] + "-" + dateString.split("-")[1];
        File file = new File(rootDir, filePath);
        file.setReadable(true);
        if (!file.exists()) {
            file.mkdirs();
        }
        String text = diaryTextStartText.getText().toString() + diaryTextContent.getText().toString();
        text = "    " + text.replaceAll("\t", "");
        Log.i(TAG, text);

        file = new File(file, fileName);
        loadDialog = DialogUtils.createLoadingDialog(MainActivity.this, "请稍候...");

        DiaryCreateThread thread = new DiaryCreateThread(this, file, text, itemid, splitWidth, splitHeight);
        Thread t1 = new Thread(thread);
        t1.start();
        return true;
    }

    class DiaryCreateThread implements Runnable {
        private File file;
        private String text;
        private int itemId;
        private MainActivity content;
        private int splitWidth;
        private int splitHeight;

        DiaryCreateThread(MainActivity content, File file, String text, int itemId, int splitWidth, int splitHeight) {
            this.file = file;
            this.text = text;
            this.itemId = itemId;
            this.content = content;
            this.splitHeight = splitHeight == 0 ? 50 : splitHeight;
            this.splitWidth = splitWidth == 0 ? 50 : splitWidth;
        }

        @Override
        public void run() {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    file.setReadable(true);
                    if (itemId == R.id.diary_create_normal) {
                        ImageUtil.coptFile(picturePath, file);
                        ImageUtil.writeMessage(file, text, 0);
                    } else if (itemId == R.id.diary_create_hide) {
                        Bitmap bmp = null;
                        File defaultBitmapFile = new File(rootDir, DF_BITMAP_PATH);
                        if (defaultBitmapFile.exists()) {
                            bmp = BitmapFactory.decodeFile(defaultBitmapFile.getPath());
                        } else {
                            bmp = BitmapFactory.decodeResource(content.getResources(), R.drawable.background);
                            defaultBitmapFile.createNewFile();
                            ImageUtil.coptFile(bmp, defaultBitmapFile);
                        }
                        ImageUtil.coptFile(bmp, file);
                        File temp = new File(rootDir, FILE_PATH + "temp");
                        if (!temp.exists()) {
                            temp.createNewFile();
                        }
                        ImageUtil.coptFile(picturePath, temp);
                        ImageUtil.writePhotoMessage(temp, file, text);
                        ImageUtil.writeMessage(file, text, 1);
                        temp.delete();
                    } else if (itemId == R.id.diary_create_split) {
                        String returnString = ImageSplitUtil.splitImage(file, picturePath, splitWidth, splitHeight);
                        ImageUtil.writeSplitMessage(returnString, file, text);
                        ImageUtil.writeMessage(file, text, 2);
                    } else if (itemId == R.id.diary_create_storage) {
                        ImageUtil.writePhotoMessage(new File(picturePath), file, text);
                        ImageUtil.writeMessage(file, text, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage(), e);
                    handler.sendEmptyMessage(2);
                }
                handler.sendEmptyMessage(0);
            } else {
                handler.sendEmptyMessage(1);
            }
        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    //生成圆角图片
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * 获取正常运行的存储设备路径
     *
     * @param activity
     * @return
     */
    public static List<String> getStoragePath(Activity activity) {
        List<String> storagePathList = new ArrayList<>();
        try {
            StorageManager sm = (StorageManager) activity.getSystemService(Activity.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm);
            for (int i = 0; i < paths.length; i++) {
                if (getStorageState(activity, paths[i]).equals(Environment.MEDIA_MOUNTED)) {
                    storagePathList.add(paths[i]);
                }
            }
        } catch (Exception e) {
            Log.e("Storage", "error");
        }
        return storagePathList;
    }

    /**
     * 获取路径状态
     *
     * @param activity
     * @param path
     * @return
     */
    public static String getStorageState(Activity activity, String path) {
        try {
            StorageManager sm = (StorageManager) activity.getSystemService(Activity.STORAGE_SERVICE);
            Method getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", new Class[]{String.class});
            String state = (String) getVolumeStateMethod.invoke(sm, path);
            return state;
        } catch (Exception e) {
            Log.e("Storage state", "error");
        }
        return "";
    }

}
