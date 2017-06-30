package com.gj.diary.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gj.diary.R;
import com.gj.diary.utils.PropertiesUtil;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryLoginDialog{

    private Context context;

    private EditText password;

    private TextView result;

    private AlertDialog diaryLoginDialog;

    public DiaryLoginDialog(final Context context) {
        this.context = context;
        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View longinDialogView = layoutInflater.inflate(R.layout.diary_login_dialog, null);
        //获取布局中的控件
        password = (EditText) longinDialogView.findViewById(R.id.diary_edit_password);
        result = (TextView) longinDialogView.findViewById(R.id.diary_password_result);


        //创建一个AlertDialog对话框
        diaryLoginDialog = new AlertDialog.Builder(context)
                .setTitle("密码登录")
                .setView(longinDialogView)       //加载自定义的对话框式样
                .setPositiveButton("确定", null)
                .setNeutralButton("取消", null)
                .create();
        diaryLoginDialog.show();
        diaryLoginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordString = password.getText().toString();
                if(passwordString == null || "".equals(passwordString)){
                    result.setText("密码不能为空！");
                }else{
                    boolean b = PropertiesUtil.checkPassword(context, passwordString, "1");
                    if(!b){
                        result.setText("密码错误！");
                    }else{
                        diaryLoginDialog.dismiss();
                    }
                }
            }
        });
    }


}
