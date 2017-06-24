package com.gj.diary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;


import com.gj.diary.R;

/**
 * Created by Administrator on 2017/6/24.
 */
public class DiaryImageView extends android.support.v7.widget.AppCompatImageView {
    /**
     * 图片比例. 比例=宽/高
     */
    private float mRatio;

    /**
     * 圆角度数
     */
    private int mRadius;

    private Paint paint;

    public DiaryImageView(Context context) {
        this(context, null);
    }

    public DiaryImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiaryImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化
     * @param context 上下文
     * @param attrs   属性
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.DiaryImageView);
        mRatio = typedArray.getFloat(R.styleable.DiaryImageView_ratio, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 宽模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 宽大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // 高大小
        int heightSize;
        // 只有宽的值是精确的才对高做精确的比例校对
        if (widthMode == MeasureSpec.EXACTLY && mRatio > 0) {
            heightSize = (int) (widthSize / mRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
