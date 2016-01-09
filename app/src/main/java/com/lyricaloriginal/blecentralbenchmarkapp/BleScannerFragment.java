package com.lyricaloriginal.blecentralbenchmarkapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by LyricalMaestro on 2016/01/09.
 */
public class BleScannerFragment extends ScannerFragment {

    private static final int REQUEST_PERMISSION = 1;

    private BluetoothLeScanner mScanner;
    private ScanCallback mCallback = new MyCallback();

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),
                        "位置情報アクセスの許可を得ました。", Toast.LENGTH_SHORT).show();
                startScan();
            } else {
                Toast.makeText(getActivity(),
                        "位置情報アクセスの許可を得ることができませんでした。", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    boolean isScanning() {
        return mScanner != null;
    }

    @Override
    void start() {
        int checkResult = PermissionChecker.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkResult != PackageManager.PERMISSION_GRANTED) {
            /*  位置情報の許可が降りていない場合*/
            requestLocaionPermissions();
        } else {
            startScan();
        }
    }

    @Override
    void stop() {
        if (mScanner != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanner.stopScan(mCallback);
            mScanner = null;
            onStopScanSuccessed();
        }
    }

    private void startScan() {
        BluetoothManager manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            Toast.makeText(getActivity(), "Bluetoohが有効ではありません。", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanner = adapter.getBluetoothLeScanner();
            mScanner.startScan(mCallback);
            onStartScanSuccessed();
        }
    }

    private void requestLocaionPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);
        } else {
            //  Android5.1未満だとそもそも許可が得られているのでここに来ることはないのだが・・。
            Toast.makeText(getActivity(),
                    "位置情報アクセスの許可を得ることができませんでした。", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private class MyCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            onScanned(new Date(), result.getRssi());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    }
}
