package com.gj.diary.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gj.diary.R;
import com.gj.diary.utils.PropertiesUtil;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryCreateDialog {

    private RadioGroup radioGroup;

    private EditText splitCreateW;

    private EditText splitCreateH;

    private LinearLayout linearLayout;

    private Context context;

    private int diaryCreateType;

    private AlertDialog diaryCreateDialog;

    public DiaryCreateDialog(@NonNull Context context) {
        this.context = context;
        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View diaryCreateView = layoutInflater.inflate(R.layout.diary_create_dialog, null);

        boolean useStorageAble = false;
        final String useStorage = PropertiesUtil.getProperties(context, "use_storage");
        if(useStorage != null && !"".equals(useStorage)  ){
            if("true".equals(useStorage)){
                useStorageAble = true;
            }
        }else{
            useStorageAble = Boolean.parseBoolean(PropertiesUtil.PROPERTIES.get("use_storage"));
        }
        if(!useStorageAble){
            final RadioButton radioButton = (RadioButton)diaryCreateView.findViewById(R.id.diary_create_storage);
            radioButton.setVisibility(View.INVISIBLE);
        }
        //获取布局中的控件
        radioGroup =(RadioGroup) diaryCreateView.findViewById(R.id.diary_create_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                diaryCreateType = checkedId;
                switch (checkedId) {
                    case R.id.diary_create_normal:
                    case R.id.diary_create_hide:
                    case R.id.diary_create_storage:
                        linearLayout.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.diary_create_split:
                        linearLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
        linearLayout = (LinearLayout)diaryCreateView.findViewById(R.id.split_create_wh);
        splitCreateW = (EditText)diaryCreateView.findViewById(R.id.split_create_w);
        splitCreateH = (EditText)diaryCreateView.findViewById(R.id.split_create_h);


        //创建一个AlertDialog对话框
        diaryCreateDialog = new AlertDialog.Builder(context)
                .setTitle("选择生成方式：")
                .setView(diaryCreateView)       //加载自定义的对话框式样
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create();
        diaryCreateDialog.show();


    }

    public void setDiaryCreateOkListener(@Nullable  View.OnClickListener diaryCreateOkListener) {
        if(diaryCreateOkListener != null){
            diaryCreateDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(diaryCreateOkListener);
        }
    }

    public int getSplitWidth(){
        int widrh = 50;
        if(splitCreateW != null){
            widrh = Integer.parseInt(splitCreateW.getText().toString());
        }
        return widrh;
    }

    public int getSplitHeight(){
        int height = 50;
        if(splitCreateH != null){
            height = Integer.parseInt(splitCreateH.getText().toString());
        }
        return height;
    }

    public int getDiaryCreateType(){
        if(diaryCreateType == 0){
            diaryCreateType = R.id.diary_create_normal;
        }
        return diaryCreateType;
    }

    public void dismiss() {
        diaryCreateDialog.dismiss();
    }
}
