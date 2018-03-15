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

public class DiaryPropertyDialog {

    private Context mContext;

    private EditText key;

    private EditText value;

    private TextView result;

    private AlertDialog diaryPropertyDialog;

    public DiaryPropertyDialog(final Context context) {
        this.mContext = context;
        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View longinDialogView = layoutInflater.inflate(R.layout.diary_property_dialog, null);
        //获取布局中的控件
        key = (EditText) longinDialogView.findViewById(R.id.diary_property_key);
        value = (EditText) longinDialogView.findViewById(R.id.diary_property_value);
        result = (TextView) longinDialogView.findViewById(R.id.diary_change_property_result);


        //创建一个AlertDialog对话框

        AlertDialog.Builder build = new AlertDialog.Builder(context)
                .setView(longinDialogView)       //加载自定义的对话框式样
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null);
        build.setTitle("修改属性值");
        diaryPropertyDialog = build.create();
        diaryPropertyDialog.show();
        diaryPropertyDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyString = key.getText().toString();
                String valueString = value.getText().toString();

                if(keyString == null || "".equals(keyString)){
                    result.setText("Key不能为空！");
                    return;
                }
                if(!PropertiesUtil.PROPERTIES.containsKey(keyString)){
                    result.setText("Key【"+keyString+"】不是可配置属相！");
                    return;
                }
                PropertiesUtil.saveProperties(mContext,keyString,valueString);
                diaryPropertyDialog.dismiss();
                ((MainActivity)mContext).handler.sendEmptyMessage(5);
            }
        });
    }

    public void show(){
        diaryPropertyDialog.show();
    }

}
