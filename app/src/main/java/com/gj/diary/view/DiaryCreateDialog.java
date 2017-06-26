package com.gj.diary.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gj.diary.R;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryCreateDialog extends Dialog {

    private RadioGroup radioGroup;

    private EditText splitCreateW;

    private EditText splitCreateH;

    private TextView diaryCreateOk;

    private TextView diaryCreateCancel;

    private LinearLayout linearLayout;

    private DiaryCreateDialog diaryCreateDialog;

    private int diaryCreateType;

    View.OnClickListener diaryCreateOkListener;

    public DiaryCreateDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.diary_create_dialog);
        setTitle("选择生成日记方式：");
        diaryCreateDialog = this;
        radioGroup =(RadioGroup) this.findViewById(R.id.diary_create_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                diaryCreateType = checkedId;
                switch (checkedId) {
                    case R.id.diary_create_normal:
                    case R.id.diary_create_hide:
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
        linearLayout = (LinearLayout)this.findViewById(R.id.split_create_wh);
        splitCreateW = (EditText)this.findViewById(R.id.split_create_w);
        splitCreateH = (EditText)this.findViewById(R.id.split_create_h);
        diaryCreateOk = (TextView)this.findViewById(R.id.diary_create_ok);
        diaryCreateOk.setOnClickListener(diaryCreateOkListener);

        diaryCreateCancel = (TextView)this.findViewById(R.id.diary_create_cancel);
        diaryCreateCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                diaryCreateDialog.dismiss();
            }
        });
    }

    public void setDiaryCreateOkListener(@Nullable  View.OnClickListener diaryCreateOkListener) {
        if(diaryCreateOkListener != null){
            this.diaryCreateOkListener = diaryCreateOkListener;
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

}
