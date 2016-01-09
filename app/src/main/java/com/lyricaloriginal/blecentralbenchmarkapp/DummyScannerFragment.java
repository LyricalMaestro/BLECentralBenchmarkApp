package com.lyricaloriginal.blecentralbenchmarkapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.Date;

/**
 * Created by LyricalMaestro on 2015/12/31.
 */
public class DummyScannerFragment extends ScannerFragment {

    private HandlerThread mThread;
    private Handler mHandler;
    private boolean mScanning = false;
    private int mIndex = 0;

    private int mInterval;

    /**
     * このクラスのインスタンスを作成します。
     *
     * @param interval センサー値取得感覚。単位はms。
     */
    static ScannerFragment newInstance(int interval) {
        if (interval < 10) {
            throw new IllegalArgumentException("intervalは10ms以上の整数を指定してください");
        }
        Bundle bundle = new Bundle();
        bundle.putInt("INTERVAL", interval);

        DummyScannerFragment f = new DummyScannerFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInterval = getArguments().getInt("INTERVAL");
        mThread = new HandlerThread("DummySensor");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScanning) {
            return;
        }
        mThread.quit();
    }

    @Override
    boolean isScanning() {
        return mScanning;
    }

    @Override
    void start() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("startはUIスレッド以外で呼び出さないでください。");
        } else if (!mThread.isAlive()) {
            throw new RuntimeException("このオブジェクトはすでに破棄されました。");
        } else if (mScanning) {
            return;
        }

        mScanning = true;
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (!mScanning) {
                    return;
                }
                final Date currentDate = new Date();
                final double value = getValue();
                onScanned(currentDate, value);
                mIndex++;
                mHandler.postDelayed(this, mInterval);
            }
        });
        onStartScanSuccessed();
    }

    @Override
    void stop() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("startはUIスレッド以外で呼び出さないでください。");
        } else if (!mThread.isAlive()) {
            throw new RuntimeException("このオブジェクトはすでに破棄されました。");
        } else if (!mScanning) {
            return;
        }

        mScanning = false;
        onStopScanSuccessed();
    }

    private double getValue() {
        return 25 * (Math.cos(Math.PI * mIndex / 31) + Math.sin(Math.PI * (mIndex + 1) / 15)) - 50;
    }
}
