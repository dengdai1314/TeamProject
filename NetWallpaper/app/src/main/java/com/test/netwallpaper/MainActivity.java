package com.test.netwallpaper;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.test.netwallpaper.service.LiveWallpaperService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 个人壁纸测试
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_set;
    private Bitmap bitmap;
    WallpaperManager wallpaperManager;
    OkHttpClient okHttpClient;
    Request request;

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //请求权限
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            initPermission();
        }
        logSentFriendRequestEvent();
    }

    private void initView(){
        bt_set = findViewById(R.id.bt_set);
        bt_set.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_set:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        if(Build.VERSION.SDK_INT >= 16)
                        {
                            intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                            intent.putExtra(
                                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    new ComponentName(getApplicationContext(), LiveWallpaperService.class)
                            );
                        }
                        else
                        {
                            intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                        }
                        startActivity(intent);
                    }
                });
                break;
        }
    }

    public void getBitmap(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                okHttpClient = new OkHttpClient(); //建立客户端
                request = new Request.Builder().url(url).build(); //封装请求
                okHttpClient.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(getApplicationContext(),"无法获取图片,请查看网络",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            try {
                                ResponseBody responseBody = okHttpClient.newCall(request).execute().body(); //执行请求等到响应体
                                InputStream inputStream = responseBody.byteStream();
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                wallpaperManager.setBitmap(bitmap);
                                Toast.makeText(getApplicationContext(),"切换壁纸成功",Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"切换壁纸失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        }).start();
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logSentFriendRequestEvent () {
        AppEventsLogger logger = AppEventsLogger.newLogger(getApplicationContext());
        logger.logEvent("sentFriendRequest");
    }


    //初始化权限
    private void initPermission(){
        mPermissionList.clear();
        for(int i=0;i<permissions.length;i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if(!mPermissionList.isEmpty()){
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissiomDismiss = false;
        if(requestCode == 1){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                    hasPermissiomDismiss = true;
                    Log.d("permission",permissions[i]);
                }
            }
        }
        if(hasPermissiomDismiss) {
            showPermissionDismiss();
        }
    }

    AlertDialog mPremissionDialog;
    private void showPermissionDismiss() {
        if(mPremissionDialog == null){
            mPremissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            Uri packageUri = Uri.parse("package:com.practise.servicebestpractice");
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageUri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            finish();
                        }
                    })
                    .create();
            mPremissionDialog.show();
        }
    }

    private void cancelPermissionDialog() {
        mPremissionDialog.cancel();
    }
}