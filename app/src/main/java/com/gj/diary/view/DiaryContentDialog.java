package com.gj.diary.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gj.diary.R;
import com.gj.diary.activity.DiaryImageZoomActivity;
import com.gj.diary.holder.DiaryTreeHolder;
import com.gj.diary.utils.ImageUtil;
import com.gj.diary.utils.PropertiesUtil;

import java.util.Map;


/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryContentDialog {

    private Context context;

    private TextView diaryContent;

    private AlertDialog diaryContentDialog;

    private Map<String, String> message;

    public DiaryContentDialog(final Context context,final DiaryTreeHolder.DiaryTreeItem diaryTreeItem) {
        this.context = context;
        if(diaryTreeItem == null){
            Toast.makeText(context, "获取日记信息失败", Toast.LENGTH_SHORT).show();
        }
        message = ImageUtil.getMessage(diaryTreeItem.getFilePath());
        String fileName = diaryTreeItem.getFileName().substring(0,10);
        String diaryMessage = message.get("message");
        if(message == null ||diaryMessage == null){
            Toast.makeText(context, "获取日记信息失败", Toast.LENGTH_SHORT).show();
        }
        String keyString = message.get( "key" );

        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View longinDialogView = layoutInflater.inflate(R.layout.diary_content_dialog, null);


        //获取布局中的控件
        diaryContent = (TextView) longinDialogView.findViewById(R.id.diary_content);
        diaryContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        diaryContent.setText(diaryMessage);
        //创建一个AlertDialog对话框

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(fileName+"日记内容：")
                .setView(longinDialogView)       //加载自定义的对话框式样
                .setPositiveButton("确定", null);
        if( keyString.endsWith( "TRUE" ) ) {
            builder.setNeutralButton("解密图片",null);
        }else if(keyString.endsWith( "TRUE1" ) ){
            builder.setNeutralButton("恢复图片",null);
        }
        diaryContentDialog =  builder.create();
        diaryContentDialog.show();

        diaryContentDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DiaryLoginDialog(context, "2");

       /*         Intent intent = new Intent(context, DiaryImageZoomActivity.class);
                intent.putExtra("image", diaryTreeItem.getFilePath());
                intent.putExtra("type","url");
                intent.putExtra("name", ((Button)view).getText());
                context.startActivity(intent);*/
            }
        });

    }



}
