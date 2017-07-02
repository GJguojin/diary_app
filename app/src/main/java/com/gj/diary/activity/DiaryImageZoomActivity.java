package com.gj.diary.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.gj.diary.R;
import com.gj.diary.view.gallery.DraftFinishView;
import com.gj.diary.view.gallery.ZoomImageView;


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
            zoomImageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            zoomImageView.setImageResource(R.mipmap.ic_launcher);
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
