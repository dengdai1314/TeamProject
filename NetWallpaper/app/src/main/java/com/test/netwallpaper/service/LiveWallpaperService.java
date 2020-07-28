package com.test.netwallpaper.service;

import android.media.MediaPlayer;
import android.os.Environment;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;

/**
 * @author DoubleTick
 * @description
 * @date 2020/7/24
 */
public class LiveWallpaperService extends WallpaperService {

    private static final String TAG = "LiveWallpaperService";

    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    public class VideoEngine extends Engine{

        private MediaPlayer mPlayer;

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.d("test","onVisibilityChanged");
            if (visible){
                mPlayer.start();
            }else {
                mPlayer.pause();
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d("test","onSurfaceCreated");
            File file = new File(Environment.getExternalStorageDirectory(),"tes.mp4");
            Log.d("test",file.getPath());
            mPlayer = new MediaPlayer();
            mPlayer.setSurface(holder.getSurface());
            try {
                mPlayer.setDataSource(file.getPath());
                mPlayer.setLooping(true);
                mPlayer.setVolume(0f,0f);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d("test","onSurfaceDestroyed");
            if(mPlayer.isPlaying()){
                mPlayer.stop();
            }
            mPlayer.release();
            mPlayer = null;
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
