package com.gj.diary.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gj.diary.R;
import com.gj.diary.adapter.GalleryPagerAdapter;
import com.gj.diary.utils.DisplayUtils;
import com.gj.diary.view.gallery.DraftFinishView;
import com.gj.diary.view.gallery.GalleryViewPager;
import com.gj.diary.view.gallery.ZoomImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhufeng on 2017/1/10.
 */

public class DiaryGalleryActivity extends DiaryBaseActivity {
    private LinearLayout actionBar = null;
    private ImageView backBtn = null;
    private TextView indexText = null;
    private TextView pictureName = null;
    private DraftFinishView galleryHolder;
    private GalleryViewPager mGallery = null;
    private GalleryPagerAdapter mAdapter = null;
    private String docName;
    private ArrayList<String> imageUrls = null;
    private int currentItem = 0;
    /**
     * 标记ActionBar当前是否出现
     */
    private boolean isActionBarShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
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
        actionBar = (LinearLayout) findViewById(R.id.actionbar_image_zoom);
        backBtn = (ImageView) findViewById(R.id.back_image_zoom);
        indexText = (TextView) findViewById(R.id.index_image_zoom);
        pictureName = (TextView) findViewById(R.id.index_image_text);
        galleryHolder = (DraftFinishView) findViewById(R.id.gallery_holder);
        mGallery = (GalleryViewPager) findViewById(R.id.zoom_image_container);
    }

    private void initData() {
        imageUrls = getIntent().getStringArrayListExtra("imageUrl");
        docName = getIntent().getStringExtra("name");
        currentItem = getIntent().getIntExtra("index", 0);
        if (imageUrls == null || imageUrls.size() == 0 || TextUtils.isEmpty(docName)) {
            finish();
        }
        try {
            String filepath= imageUrls.get(currentItem);
            File file = new File(filepath);
            pictureName.setText( file.getName().substring(0,10));
        }catch (Exception e){
            pictureName.setText("");
        }
        indexText.setText(docName);
        mAdapter = new GalleryPagerAdapter(mContext, imageUrls);
        mGallery.setPageMargin(DisplayUtils.dp2Px(10));
        mGallery.setAdapter(mAdapter);
        mGallery.setOffscreenPageLimit(1);
        mGallery.setCurrentItem(currentItem, false);
        galleryHolder.setZoomView(mGallery);
    }

    private void initEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mGallery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ZoomImageView zoomImage = mAdapter.getZoomImageByIndex(currentItem);
                zoomImage.reset();
                currentItem = position;
                indexText.setText(docName);

                try {
                    String filepath= imageUrls.get(currentItem);
                    File file = new File(filepath);
                    pictureName.setText( file.getName().substring(0,10));
                }catch (Exception e){
                    pictureName.setText("");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == 0 && currentItem == 0){
                    Toast.makeText(DiaryGalleryActivity.this, "到头了", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGallery.setOnCustomClickListener(new GalleryViewPager.OnCustomClickListener() {
            @Override
            public void onSingleTap() {
                if (isActionBarShow) {
                    hideActionBar();
                } else {
                    showActionBar();
                }
            }
        });

        galleryHolder.setOnFinishListener(new DraftFinishView.OnFinishListener() {
            @Override
            public void onFinish() {
                finish();
            }
        });
    }

    /**
     * 动画展示actionbar
     */
    private void showActionBar() {
        isActionBarShow = true;
        actionBar.setVisibility(View.VISIBLE);
        Animation startAnim = AnimationUtils.loadAnimation(this, R.anim.action_bar_show);
        startAnim.setFillAfter(true);
        actionBar.startAnimation(startAnim);
    }

    /**
     * 动画隐藏actionbar
     */
    private void hideActionBar() {
        isActionBarShow = false;
        Animation hideAnim = AnimationUtils.loadAnimation(this, R.anim.action_bar_hide);
        hideAnim.setFillAfter(true);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                actionBar.setVisibility(View.GONE);
                actionBar.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        actionBar.startAnimation(hideAnim);
    }

}
