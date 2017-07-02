package com.gj.diary.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gj.diary.R;
import com.gj.diary.activity.MainActivity;
import com.gj.diary.utils.PropertiesUtil;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryChangePassDialog {

    private Context mContext;

    private EditText oldPassword;

    private EditText newPassword;

    private EditText newPasswordRepeat;

    private TextView result;

    private AlertDialog diaryChangePassDialog;

    private String type;

    public DiaryChangePassDialog(final Context context, final String type) {
        this.mContext = context;
        this.type = type;
        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View longinDialogView = layoutInflater.inflate(R.layout.diary_update_password_dialog, null);
        //获取布局中的控件
        oldPassword = (EditText) longinDialogView.findViewById(R.id.diary_old_password);
        newPassword = (EditText) longinDialogView.findViewById(R.id.diary_new_password);
        newPasswordRepeat = (EditText) longinDialogView.findViewById(R.id.diary_new_password_repeat);
        result = (TextView) longinDialogView.findViewById(R.id.diary_change_password_result);


        //创建一个AlertDialog对话框

        AlertDialog.Builder build = new AlertDialog.Builder(context)
                .setView(longinDialogView)       //加载自定义的对话框式样
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null);
        if("1".equals(type)){
            build.setTitle("修改登录密码");
        }else{
            build.setTitle("修改解析密码");
        }
        diaryChangePassDialog = build.create();
        diaryChangePassDialog.show();
        diaryChangePassDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPasswordString = newPassword.getText().toString();
                String newPasswordRepeatString = newPasswordRepeat.getText().toString();
                String oldPasswordString = oldPassword.getText().toString();

                if(oldPasswordString == null || "".equals(oldPasswordString)){
                    result.setText("旧密码不能为空！");
                    return;
                }
                String typeString ="";
                if("2".equals(type)){
                    typeString = PropertiesUtil.PASSWORD_PHOTO;
                }else{
                    typeString = PropertiesUtil.PASSWORD_DIARY;
                }

                if(newPasswordString == null || "".equals(newPasswordString)){
                    result.setText("新密码不能为空！");
                    return;
                }

                if(newPasswordRepeatString == null || "".equals(newPasswordRepeatString)){
                    result.setText("请再次输入新密码！");
                    return;
                }

                if(!newPasswordRepeatString.equals(newPasswordString)){
                    result.setText("两次密码不相同！");
                    return;
                }

                final String properties = PropertiesUtil.getProperties(mContext, typeString);
                if(!PropertiesUtil.getMd5String(oldPasswordString).equals(properties)){
                    result.setText("原密码输入错误！");
                    return;
                }
                PropertiesUtil.diaryPassword = null;

                PropertiesUtil.saveProperties(mContext,typeString,newPasswordString);
                diaryChangePassDialog.dismiss();
                ((MainActivity)mContext).handler.sendEmptyMessage(4);
            }
        });
    }

    public void show(){
        diaryChangePassDialog.show();
    }

}
