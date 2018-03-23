package com.gj.diary.view;

import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gj.diary.R;
import com.gj.diary.activity.DiaryQueryActivity;
import com.gj.diary.utils.PropertiesUtil;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryLoginDialog{
    private static final String TAG = "DiaryLoginDialog";

    private Context mContext;

    private EditText password;

    private TextView result;

    private AlertDialog diaryLoginDialog;

    private FingerprintManagerCompat manager;

    private String type;

    public DiaryLoginDialog(final Context context, final String type) {
        this.mContext = context;
        this.type = type;

        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View longinDialogView = layoutInflater.inflate(R.layout.diary_login_dialog, null);

        boolean fingerprintable = false;
        final String useFingerprint = PropertiesUtil.getProperties(context, "use_fingerprint");
        if(useFingerprint != null && !"".equals(useFingerprint)){
            if("true".equalsIgnoreCase(useFingerprint)){
                fingerprintable =true;
            }
        }else{
            PropertiesUtil.saveProperties(context,"use_fingerprint",PropertiesUtil.PROPERTIES.get("use_fingerprint"));
        }

        if(fingerprintable){
            manager = FingerprintManagerCompat.from(context);
            if(manager != null){
                manager.authenticate(null, 0, null, new FingerprintCallBack(type), null);
                final View fingerprintImage = longinDialogView.findViewById(R.id.diary_fingerprint);
                fingerprintImage.setVisibility(View.VISIBLE);
            }else{
                fingerprintable = false;
            }
        }
        //获取布局中的控件
        password = (EditText) longinDialogView.findViewById(R.id.diary_edit_password);
        result = (TextView) longinDialogView.findViewById(R.id.diary_password_result);
        result.setText("");

        //创建一个AlertDialog对话框
        AlertDialog.Builder build = new AlertDialog.Builder(context)
                .setView(longinDialogView)       //加载自定义的对话框式样
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null);
        if("1".equals(type) || "0".equals(type)){
            build.setTitle("登录密码"+(fingerprintable?"或指纹":""));
        }else{
            build.setTitle("解析密码"+(fingerprintable?"或指纹":""));
        }
        diaryLoginDialog = build.create();
        diaryLoginDialog.show();
        diaryLoginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordString = password.getText().toString();
                if(passwordString == null || "".equals(passwordString)){
                    result.setText("密码不能为空！");
                }else{
                    boolean b = PropertiesUtil.checkPassword(context, passwordString,type);
                    if(!b){
                        result.setText("密码错误！");
                    }else{
                        diaryLoginDialog.dismiss();
                        if("0".equals(type)){
                            new AlertDialog.Builder(mContext).setTitle("产品秘钥：").setMessage(PropertiesUtil.getProperties(mContext, "salt_value")).show();
                        }else if("1".equals(type)){
                            DiaryQueryActivity diaryQueryActivity = (DiaryQueryActivity) context;
                            PropertiesUtil.diaryPassword =passwordString;
                            diaryQueryActivity.handler.sendEmptyMessage(DiaryQueryActivity.PASSWORD_CHECKED_OK);
                        } else{
                            DiaryQueryActivity diaryQueryActivity = (DiaryQueryActivity) context;
                            diaryQueryActivity.handler.sendEmptyMessage(DiaryQueryActivity.PASSWORD_PHOTO_OK);
                        }
                    }
                }
            }
        });
    }

    public void show(){
        diaryLoginDialog.show();
    }


    class FingerprintCallBack extends FingerprintManagerCompat.AuthenticationCallback {
        private static final String TAG = "FingerprintCallBack";
        private String type;

        public FingerprintCallBack(String type) {
            this.type = type;
        }

        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Log.d(TAG, "onAuthenticationError: " + errString);
            result.setText(errString);
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed: " + "验证失败");
            result.setText("指纹验证失败！");
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Log.d(TAG, "onAuthenticationHelp: " + helpString);
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            Log.d(TAG, "onAuthenticationSucceeded: " + "验证成功");
            diaryLoginDialog.dismiss();
            if("0".equals(type)){
                new AlertDialog.Builder(mContext).setTitle("产品秘钥：").setMessage(PropertiesUtil.getProperties(mContext, "salt_value")).show();
            }else if("1".equals(type)){
                DiaryQueryActivity diaryQueryActivity = (DiaryQueryActivity) mContext;
                diaryQueryActivity.handler.sendEmptyMessage(DiaryQueryActivity.PASSWORD_CHECKED_OK);
                PropertiesUtil.hasFingerprint = true;
            } else{
                DiaryQueryActivity diaryQueryActivity = (DiaryQueryActivity) mContext;
                diaryQueryActivity.handler.sendEmptyMessage(DiaryQueryActivity.PASSWORD_PHOTO_OK);
            }
        }
    }

}
