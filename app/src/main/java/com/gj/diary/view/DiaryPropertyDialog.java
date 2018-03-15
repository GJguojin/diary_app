package com.gj.diary.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gj.diary.R;
import com.gj.diary.activity.MainActivity;
import com.gj.diary.utils.PropertiesUtil;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryPropertyDialog {

    private Context mContext;

    private EditText key;

    private EditText value;

    private TextView result;

    private AlertDialog diaryPropertyDialog;

    private String keyString;

    public DiaryPropertyDialog(final Context context) {
        this.mContext = context;
        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View longinDialogView = layoutInflater.inflate(R.layout.diary_property_dialog, null);
        //获取布局中的控件
        key = (EditText) longinDialogView.findViewById(R.id.diary_property_key);
        value = (EditText) longinDialogView.findViewById(R.id.diary_property_value);
        result = (TextView) longinDialogView.findViewById(R.id.diary_change_property_result);

        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Object[] objects = PropertiesUtil.PROPERTIES.keySet().toArray();
                final String[] items = new String[objects.length];
                final String[] itemStrings = new String[objects.length];
                for(int i=0;i<items.length;i++ ){
                    items[i] = (String)objects[i];
                }
                for(int i=0;i<items.length;i++ ){
                    itemStrings[i] =   PropertiesUtil.PROPERTIES_NAME.get(items[i])+"["+items[i]+"]";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                // 设置参数
                builder.setTitle("选择属相值：")
                        .setItems(itemStrings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                keyString =  items[which];
                                key.setText(itemStrings[which]);
                                final String properties = PropertiesUtil.getProperties(mContext, keyString);
                                if(properties != null && !"".equals(properties)){
                                    value.setText(properties);
                                }else{
                                    value.setText(PropertiesUtil.PROPERTIES.get(keyString));
                                }

                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        //创建一个AlertDialog对话框

        AlertDialog.Builder build = new AlertDialog.Builder(context)
                .setView(longinDialogView)       //加载自定义的对话框式样
                .setPositiveButton("确定", null)
                .setNeutralButton("回复默认",null)
                .setNegativeButton("退出", null);
        build.setTitle("修改属性值");
        diaryPropertyDialog = build.create();
        diaryPropertyDialog.show();
        diaryPropertyDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                ((MainActivity)mContext).handler.sendEmptyMessage(5);
            }
        });
        diaryPropertyDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyString != null && !"".equals(keyString)){
                    value.setText(PropertiesUtil.PROPERTIES.get(keyString));
                }
            }
        });
        diaryPropertyDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)mContext).recreate();
                diaryPropertyDialog.dismiss();
            }
        });
    }

    public void show(){
        diaryPropertyDialog.show();
    }

}
