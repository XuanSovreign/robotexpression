package com.space.hwexpression;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by licht on 2019/9/11.
 */

public class TcpConnectionServer extends Service {
//    private Handler mHandler = new Handler();
//    private Runnable task = new Runnable() {
//        @Override
//        public void run() {
//            mHandler.removeCallbacks(this);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    TcpUtil.getInstance("192.168.30.172", 2000);
//                    TcpUtil.sendData();
//                }
//            }).start();
//            mHandler.postDelayed(this, 1000);
//        }
//    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        mHandler.post(task);
        SharedPreferences mPreferences = getSharedPreferences("robot_ip", MODE_PRIVATE);
        final String ipAddress = mPreferences.getString("ip_address", "");
        Log.e("ddd", "onStartCommand: "+ipAddress );
        if (TextUtils.isEmpty(ipAddress)) {
            stopSelf();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                TcpUtil.getInstance(ipAddress,2000);
                TcpUtil.receiveData();
            }
        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                TcpUtil.getInstance("192.168.30.172",2000);
//                while (true) {
//                    try {
//                        TcpUtil.sendData();
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        if (mHandler != null) {
//            mHandler.removeCallbacks(task);
//        }
        super.onDestroy();
    }
}
