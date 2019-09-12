package com.space.hwexpression;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends Activity {
    private Handler mHandler = new Handler();
    private static final String TAG = MainActivity.class.getName();
    private int mDuration;
    private ImageView mIvExpression;
    private Runnable waitingExpression = new Runnable() {
        @Override
        public void run() {
            loadGifImage(R.mipmap.test,R.mipmap.iv_one);
            mHandler.postDelayed(this, 20000);
        }
    };
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mIvExpression = findViewById(R.id.iv_expression);
        mHandler.postDelayed(waitingExpression,20000);
        EventBus.getDefault().register(this);
        mIntent = new Intent(this, TcpConnectionServer.class);
        startService(mIntent);
    }

    /**
     *加载gif图片
     * @param resId gif的资源id
     * @param resId2  普通图片id
     */
    private void loadGifImage(int resId, final int resId2) {
        GlideApp.with(getApplication()).asGif().load(resId).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.setLoopCount(1);
                int duration = 0;
                Drawable.ConstantState constantState = resource.getConstantState();
                if (constantState != null) {
                    Object frameLoader = ValueArg.getValue(constantState, "frameLoader");
                    if (frameLoader != null) {
                        Object gifDecoder = ValueArg.getValue(frameLoader, "gifDecoder");
                        if (gifDecoder != null && gifDecoder instanceof GifDecoder) {
                            for (int i = 0; i < ((GifDecoder) gifDecoder).getFrameCount(); i++) {
                                duration += ((GifDecoder) gifDecoder).getDelay(i);
                            }
                            mDuration = duration;
                            Log.e(TAG, "onResourceReady: duration=" + duration);
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadImage(resId2);
                            }
                        }, mDuration+100);
                    }
                }
                return false;
            }
        }).into(mIvExpression);

    }

    private void loadImage(int resId) {
        GlideApp.with(getApplication()).load(resId).placeholder(R.mipmap.iv_bg).dontAnimate().into(mIvExpression);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String  event) {
        if (event.contains("start")) {
//            loadGifImage();
            loadImage(R.mipmap.iv_two);
            mHandler.removeCallbacks(waitingExpression);
        }
        if (event.contains("end")) {
//            loadGifImage();
            loadImage(R.mipmap.iv_three);
            mHandler.postDelayed(waitingExpression,20000);
        }
    }

    @Override
    protected void onStop() {
        if (isFinishing()) {
            mHandler.removeCallbacks(waitingExpression);
            stopService(mIntent);
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }
}
