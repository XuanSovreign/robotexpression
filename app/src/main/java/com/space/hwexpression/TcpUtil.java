package com.space.hwexpression;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by licht on 2019/9/10.
 */

public class TcpUtil {
    private static Socket sSocket = null;
    private static String mIp;
    private static int mPort;
    private static InputStream inputStream;
    private static final String TAG = TcpUtil.class.getName();
    private static OutputStream outputStream;

    public synchronized static Socket getInstance(String ip, int port) {
        try {
            if (sSocket == null) {
                sSocket = new Socket(ip, port);
                mIp = ip;
                mPort = port;
            }
            return sSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void receiveData() {
        if (sSocket == null) {
            getInstance(mIp, mPort);
        }
        try {
            if (inputStream == null) {
                inputStream = sSocket.getInputStream();
            }
            sSocket.sendUrgentData(0);
            byte[] bytes = new byte[10];
            int len = 0;
            while ((len = inputStream.read(bytes)) > 0) {
                String text = new String(bytes, 0, len);
                EventBus.getDefault().post(text);
                Log.e(TAG, "receiveData: receive=" + text);
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeSocket();
            receiveData();
        }
    }

    private static void closeSocket() {

        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (sSocket != null) {
                sSocket.close();
            }
            sSocket = null;
            outputStream = null;
            inputStream = null;
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public static void sendData() {
        if (sSocket == null) {
            getInstance(mIp, mPort);
        }
        try {
            if (outputStream == null) {
                outputStream = sSocket.getOutputStream();
            }
            outputStream.write(0);
        } catch (IOException e) {
            e.printStackTrace();
            closeSocket();
        }
    }



}
