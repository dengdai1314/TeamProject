package com.test.netwallpaper.service;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import com.test.netwallpaper.Constant;

import java.io.IOException;

import androidx.preference.PreferenceManager;

/**
 * @author DoubleTick
 * @description
 * @date 2020/7/24
 */
public class LiveWallpaperService extends WallpaperService {

    private static final String SERVICE_NAME = "com.test.netwallpaper.service.LiveWallpaperService";
    private static final String TAG = "LiveWallpaperService";

    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    public static void startWallPaper(Context context, String videoPath) {
        WallpaperInfo info = WallpaperManager.getInstance(context).getWallpaperInfo();

        if(info != null && SERVICE_NAME.equals(info.getServiceName())) {
            changeVideo(context, videoPath);
        } else {
            startNewWallpaper(context, videoPath);
        }
    }

    public static void closeWallpaper(Context context) {
        try {
            WallpaperManager.getInstance(context).clear();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void startNewWallpaper(Context context, String path) {
        saveVideoPath(context, path);
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, VideoWallPaperService.class));
        context.startActivity(intent);
    }

    private static void changeVideo(Context context, String path) {
        saveVideoPath(context, path);
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION);
        intent.putExtra(Constant.BROADCAST_SET_VIDEO_PARAM, Constant.ACTION_SET_VIDEO);
        context.sendBroadcast(intent);
    }

    public static void setVolume(Context context, boolean hasVolume) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION);
        if(hasVolume) {
            intent.putExtra(Constant.BROADCAST_SET_VIDEO_PARAM, Constant.ACTION_VOICE_NORMAL);
        } else {
            intent.putExtra(Constant.BROADCAST_SET_VIDEO_PARAM, Constant.ACTION_VOICE_SILENCE);
        }
        context.sendBroadcast(intent);
    }

    private static void saveVideoPath(Context context, String path) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(Constant.VIDEO_PATH, path);
        editor.apply();
    }

    private String getVideoPath() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(Constant.VIDEO_PATH, "/storage/emulated/0/tes.mp4");
    }

    private class VideoEngine extends Engine implements MediaPlayer.OnPreparedListener,
            MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        private MediaPlayer mPlayer;
        private boolean mLoop;
        private boolean mVolume;
        private boolean isPapered = false;

        private VideoEngine() {
            SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(LiveWallpaperService.this);
            mLoop = preferences.getBoolean("loop", true);
            mVolume = preferences.getBoolean("volume", false);
        }

        private BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int action = intent.getIntExtra(Constant.BROADCAST_SET_VIDEO_PARAM, -1);
                switch(action) {
                    case Constant.ACTION_SET_VIDEO: {
                        setVideo(getVideoPath());
                        break;
                    }
                    case Constant.ACTION_VOICE_NORMAL: {
                        mVolume = true;
                        setVolume();
                        break;
                    }
                    case Constant.ACTION_VOICE_SILENCE: {
                        mVolume = false;
                        setVolume();
                        break;
                    }
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.ACTION);
            registerReceiver(mReceiver, filter);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(mReceiver);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if(isPapered) {
                if(visible) {
                    mPlayer.start();
                } else {
                    mPlayer.pause();
                }
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mPlayer = new MediaPlayer();
            setVideo(getVideoPath());
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if(mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
            mPlayer = null;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            isPapered = true;
            mp.start();
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            closeWallpaper(getApplicationContext());
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            closeWallpaper(getApplicationContext());
            return true;
        }

        private void setVideo(String videoPath) {
            if(TextUtils.isEmpty(videoPath)) {
                closeWallpaper(getApplicationContext());
                throw new IllegalArgumentException("video path is null");
            }
            if(mPlayer != null) {
                mPlayer.reset();
                isPapered = false;
                try {
                    mPlayer.setOnPreparedListener(this);
                    mPlayer.setOnCompletionListener(this);
                    mPlayer.setOnErrorListener(this);
                    mPlayer.setLooping(mLoop);
                    //                  mPlayer.setDisplay(getSurfaceHolder());
                    mPlayer.setSurface(getSurfaceHolder().getSurface());
                    setVolume();
                    mPlayer.setDataSource(videoPath);
                    mPlayer.prepareAsync();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void setVolume() {
            if(mPlayer != null) {
                if(mVolume) {
                    mPlayer.setVolume(1.0f, 1.0f);
                } else {
                    mPlayer.setVolume(0f, 0f);
                }
            }
        }
    }
//    class MyEngine extends Engine{
//        private final Paint paint = new Paint();
//
//        @Override
//        public void onCreate(SurfaceHolder surfaceHolder) {
//            super.onCreate(surfaceHolder);
//            paint.setColor(getResources().getColor(R.color.colorRed));
//            paint.setTextSize(60f);
//            paint.setAntiAlias(true);
//            paint.setTextAlign(Paint.Align.CENTER);
//        }
//
//        @Override
//        public void onVisibilityChanged(boolean visible) {
//            super.onVisibilityChanged(visible);
//            if(visible){
//                SurfaceHolder holder = MyEngine.this.getSurfaceHolder();
//                Canvas canvas = holder.lockCanvas(null);
//                canvas.save();
//                canvas.translate((canvas.getWidth()/2),(canvas.getHeight()/2));
//                canvas.drawText("hong kongis part of China!",0f,0f,paint);
//                holder.unlockCanvasAndPost(canvas);
//            }
//        }
//    }
}
