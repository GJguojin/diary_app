package com.gj.diary.view;

import android.app.Application;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gj.diary.R;
import com.gj.diary.activity.MainActivity;
import com.gj.diary.utils.PropertiesUtil;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryThemeDialog {

    private RadioGroup radioGroup;

    private Context mContext;

    private AlertDialog diaryThemeDialog;

    public DiaryThemeDialog(@NonNull Context context) {
        this.mContext = context;
        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View diaryCreateView = layoutInflater.inflate(R.layout.diary_theme_dialog, null);

        //获取布局中的控件
        radioGroup =(RadioGroup) diaryCreateView.findViewById(R.id.diary_theme_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                int appTheme = 0;
                switch(checkedId){
                    case R.id.diary_theme_blue:
                        appTheme =  R.style.BlueTheme;
                        break;
                    case R.id.diary_theme_dark:
                        appTheme =  R.style.DarkTheme;
                        break;
                    case R.id.diary_theme_pink:
                        appTheme =  R.style.PinkTheme;
                        break;
                    case R.id.diary_theme_red:
                        appTheme =  R.style.RedTheme;
                        break;
                    case R.id.diary_theme_yellow:
                        appTheme =  R.style.YellowTheme;
                        break;
                    case R.id.diary_theme_green:
                        appTheme =  R.style.GreenTheme;
                        break;
                    default:
                        appTheme = R.style.BlueTheme;
                        break;
                }
                PropertiesUtil.saveProperties(mContext,"appTheme",""+appTheme);
                diaryThemeDialog.dismiss();
                ((AppCompatActivity)mContext).recreate();
            }
        });


        //创建一个AlertDialog对话框
        diaryThemeDialog = new AlertDialog.Builder(context)
                .setTitle("切换主题：")
                .setView(diaryCreateView)       //加载自定义的对话框式样
//                .setPositiveButton("确定", null)
//                .setNegativeButton("取消", null)
                .create();
        diaryThemeDialog.show();
    }




    public void dismiss() {
        diaryThemeDialog.dismiss();
    }
}
