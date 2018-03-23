package com.gj.diary.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.gj.diary.R;
import com.gj.diary.utils.ImageSplitUtil;
import com.gj.diary.utils.ImageUtil;
import com.gj.diary.utils.PropertiesUtil;
import com.gj.diary.view.gallery.DraftFinishView;
import com.gj.diary.view.gallery.ZoomImageView;

import java.io.File;
import java.util.Map;


/**
 * Created by zhufeng on 2016/11/1.
 */
public class DiaryImageZoomActivity extends DiaryBaseActivity {
    private final String TAG = "DiaryImageZoomActivity";
    private ZoomImageView zoomImageView = null;
    private DraftFinishView galleryHolder;

    /**
     * 文档名称
     */
    private String titleName;
    /**
     * 展示的图片（URL或者地址）
     */
    private String imageResource;
    private String imageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String appTheme = PropertiesUtil.getProperties(this, "appTheme");
        if (appTheme != null && !"".equals(appTheme)) {
            this.setTheme(Integer.parseInt(appTheme));
        }
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        initView();
        initData();
        initEvents();
    }

    @Override
    protected boolean customEnterAnimation() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    @Override
    protected boolean customExitAnimation() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        galleryHolder = (DraftFinishView) findViewById(R.id.gallery_holder);
        zoomImageView = (ZoomImageView) findViewById(R.id.image_image_zoom);
    }

    private void initData() {
        imageResource = getIntent().getStringExtra("image");
        imageType = getIntent().getStringExtra("type");
        titleName = getIntent().getStringExtra("name");
        if (TextUtils.isEmpty(imageResource) || TextUtils.isEmpty(imageType) || TextUtils.isEmpty(titleName)) {
            finish();
        }
        getSupportActionBar().setTitle(titleName);

        if (imageType.equals("url")) {
            if (PropertiesUtil.diaryPassword != null || PropertiesUtil.hasFingerprint) {
                try {
                    Map<String, String> message = ImageUtil.getMessage(imageResource);
                    String keyString = message.get("key");
                    if (keyString.endsWith("TRUE")) {
                        File photoMessage = ImageUtil.getPhotoMessage(imageResource, keyString);
                        Bitmap bitmap = BitmapFactory.decodeFile(photoMessage.getPath());
                        zoomImageView.setImageBitmap(bitmap);
                    } else if (keyString.endsWith("TRUE1")) {
                        Bitmap bitmap = ImageSplitUtil.mergeImage(imageResource, keyString);
                        zoomImageView.setImageBitmap(bitmap);
                    } else {
                        zoomImageView.setImageResource(R.drawable.background);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    zoomImageView.setImageResource(R.drawable.background);
                }
            }else{
                zoomImageView.setImageResource(R.drawable.background);
            }
        } else {
            zoomImageView.setImageResource(R.drawable.background);
        }
        galleryHolder.setZoomView(zoomImageView);
    }


    private void initEvents() {
        galleryHolder.setOnFinishListener(new DraftFinishView.OnFinishListener() {
            @Override
            public void onFinish() {
                finish();
            }
        });
    }

}
