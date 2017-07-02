package com.gj.diary.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.gj.diary.R;
import com.gj.diary.utils.DialogUtils;
import com.gj.diary.utils.ImageSplitUtil;
import com.gj.diary.utils.ImageUtil;
import com.gj.diary.utils.PropertiesUtil;
import com.gj.diary.view.DiaryChangePassDialog;
import com.gj.diary.view.DiaryCreateDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends DiaryBaseActivity implements View.OnClickListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static File rootDir;

    private static final String TAG = "MainActivity";
    private static final String IMAGE_UNSPECIFIED = "image/*";
    public static String FILE_PATH = "/diary/日记/picture/";
    public static String DF_BITMAP_PATH = "/diary/defaultBitmap.jpg";

    private static String diaryStartText = "\t\t\t\t这张照片还记得吗？这一天是[date],";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private static String dateString;


    private Button dirayCreateButton = null; //生成日志按钮
    private Button diaryQueryButton = null;

    private TextView diaryTextStartText = null;

    private EditText diaryTextContent;

    private String picturePath;
    private ImageView diaryPicture;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.diary);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("日记制作");
        actionBar.show();

        String filePath = PropertiesUtil.getProperties(this, "filePath");
        if (filePath != null && !"".equals(filePath)) {
            FILE_PATH = filePath;
        } else {
            PropertiesUtil.saveProperties(this, "filePath", FILE_PATH);
        }

        String defaultBitmapPath = PropertiesUtil.getProperties(this, "defaultBitmapPath");
        if (defaultBitmapPath != null && !"".equals(defaultBitmapPath)) {
            DF_BITMAP_PATH = defaultBitmapPath;
        } else {
            PropertiesUtil.saveProperties(this, "defaultBitmapPath", DF_BITMAP_PATH);
        }

        //设置日记时间
        diaryTextStartText = (TextView) this.findViewById(R.id.diary_text_start);
        Date date = new Date();
        String format = sdf.format(date);
        dateString = sdf1.format(date);
        diaryTextStartText.setText(diaryStartText.replace("[date]", format));
        diaryTextStartText.setOnClickListener(this);

        diaryTextContent = (EditText) this.findViewById(R.id.diary_text_content);

        //生成日记方法
        dirayCreateButton = (Button) this.findViewById(R.id.diray_create);
        dirayCreateButton.setOnClickListener(this);

        //浏览日记方法
        diaryQueryButton = (Button) this.findViewById(R.id.diary_query);
        diaryQueryButton.setOnClickListener(this);

        diaryPicture = (ImageView) findViewById(R.id.diary_picture);
        Bitmap defaultDiaryPicture = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        defaultDiaryPicture = getRoundedCornerBitmap(defaultDiaryPicture, 70);
        diaryPicture.setImageBitmap(defaultDiaryPicture);
        diaryPicture.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.chang_login_pass_menu :
                new DiaryChangePassDialog(this,"1");
                break;
            case R.id.chang_photo_pass_menu :
                new DiaryChangePassDialog(this,"2");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                picturePath = null;
                if (data == null) {
                    return;
                }
                picturePath = getRealFilePath(this, data.getData());
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
                Intent openPicture = new Intent(Intent.ACTION_PICK, null);
                openPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                //openPicture.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(openPicture, 1);
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
        String filePath = FILE_PATH + dateString.split("-")[0] + "/" + dateString.split("-")[0] + "-" + dateString.split("-")[1];
        File file = new File(rootDir, filePath);
        file.setReadable(true);
        if (!file.exists()) {
            file.mkdirs();
        }
        String text = diaryTextStartText.getText().toString() + diaryTextContent.getText().toString();
        text = "    " + text.replaceAll("\t", "");
        Log.i(TAG, text);

        file = new File(file, dateString + ".jpg");
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
                        File defaultBitmapFile = new File(rootDir, "/diary/defaultBitmap.jpg");
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
}
