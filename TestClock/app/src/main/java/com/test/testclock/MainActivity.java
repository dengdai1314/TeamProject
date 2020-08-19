package com.test.testclock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.test.testclock.View.ClockView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends Activity implements View.OnClickListener {
    private ClockView clockView;
    private ConstraintLayout ll_clock;
    private TextView tv_time;
    private TextView tv_date;
    private Button bt_select;
    private Button bt_set;

    //获取当前时间
    private Calendar curTime =Calendar.getInstance();
    int hour = curTime.get(Calendar.HOUR);
    int minute = curTime.get(Calendar.MINUTE);
    int second = curTime.get(Calendar.SECOND);
    int year = curTime.get(Calendar.YEAR);
    int month = curTime.get(Calendar.MONTH)+1;
    int date = curTime.get(Calendar.DATE);

    private IntentFilter intentFilter;
    private TimeChangeReceiver timeChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initReceiver();
        clockView.start();
        clockView.setTime(hour,minute,second,curTime);
    }

    private void initView(){
        tv_time = findViewById(R.id.tv_time);
        tv_date = findViewById(R.id.tv_date);
        ll_clock = findViewById(R.id.ll_clock);
        clockView = findViewById(R.id.cv_clock);
        bt_select = findViewById(R.id.bt_select);
//        bt_set = findViewById(R.id.bt_set);
        bt_select.setOnClickListener(this);
//        bt_set.setOnClickListener(this);
        tv_date.setText(year+"/"+month+"/"+date);
        cgTimeText();
    }

    private void initReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_select:
                break;
            case R.id.bt_set:
                break;
            default:break;
        }
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    //每过一分钟 触发
                    cgTimeText();
                    break;
                default:
                    break;
            }
        }
    }



    private void cgTimeText(){
        Date date = new Date();
        String strDateFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        tv_time.setText(sdf.format(date));
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeChangeReceiver);
    }
}