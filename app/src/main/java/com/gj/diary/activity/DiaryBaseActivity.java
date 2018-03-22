package com.gj.diary.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.gj.diary.MyApp;
import com.gj.diary.utils.PropertiesUtil;

/**
 * Created by baiyin on 2016/5/30.
 */
public class DiaryBaseActivity extends AppCompatActivity {
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!customEnterAnimation()) {
//            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        mContext = MyApp.getAppContext();
    }

    protected boolean customExitAnimation(){
        return false;
    }

    protected boolean customEnterAnimation(){
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        if(!customExitAnimation()) {
//            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
            }
        }
        return true;
    }

}
