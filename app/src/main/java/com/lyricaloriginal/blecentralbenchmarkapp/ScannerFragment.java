package com.lyricaloriginal.blecentralbenchmarkapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import java.util.Date;

/**
 * Beaconの電波を受信するためのScannerのフラグメント
 * <p/>
 * Created by LyricalMaestro on 2015/12/31.
 */
abstract class ScannerFragment extends Fragment {

    private Listener mListener = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Listener) {
            mListener = (Listener) activity;
        }
    }

    /**
     * @param date
     * @param rssi
     */
    protected final void onScanned(final Date date, final double rssi) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onScanned(date, rssi);
                }
            }
        });
    }

    protected final void onStartScanSuccessed(){
        if(mListener != null){
            mListener.onStartScanSuccessed();
        }
    }

    protected final void onStopScanSuccessed(){
        if(mListener != null){
            mListener.onStopScan();
        }
    }

    abstract boolean isScanning();

    abstract void start();

    abstract void stop();

    interface Listener {
        void onStartScanSuccessed();
        void onScanned(Date date, double rssi);
        void onStopScan();
    }
}
