package com.lyricaloriginal.blecentralbenchmarkapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * メインアクティビティ
 */
public class MainActivity extends AppCompatActivity implements ScannerFragment.Listener {

    private static int MAX_Y_VALUE = 0;
    private static int MIN_Y_VALUE = -100;

    private ScannerFragment mScanner;
    private LineChart mChart;
    private Button mScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChart = (LineChart) findViewById(R.id.chart);
        initLineChart(mChart);

        mScanButton = (Button) findViewById(R.id.scan_btn);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScanner.isScanning()) {
                    mScanner.stop();
                } else {
                    mScanner.start();
                }
            }
        });
        findViewById(R.id.scan_report_list_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "このボタンはまだ実装していません。",
                        Toast.LENGTH_SHORT).show();
            }
        });
        if (savedInstanceState == null) {
            mScanner = new BleScannerFragment();
            getFragmentManager().beginTransaction().
                    add(mScanner, ScannerFragment.class.getName()).commit();
        } else {
            mScanner = (ScannerFragment) getFragmentManager()
                    .findFragmentByTag(ScannerFragment.class.getName());
        }
    }

    @Override
    public void onStartScanSuccessed() {
        mScanButton.setText(R.string.scan_stop);
    }

    @Override
    public void onScanned(Date date, double rssi) {
        LineData data = mChart.getData();
        LineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = new LineDataSet(null, "サンプルデータ");
            set.setColor(Color.BLUE);
            set.setDrawValues(false);
            data.addDataSet(set);
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        data.addXValue(format.format(date));
        data.addEntry(new Entry((float) rssi, set.getEntryCount()), 0);

        mChart.notifyDataSetChanged();
        mChart.setVisibleXRangeMaximum(13);
        mChart.moveViewToX(data.getXValCount() - 14);   //  移動する
    }

    @Override
    public void onStopScan() {
        mScanButton.setText(R.string.scan_start);
    }

    private void initLineChart(LineChart chart) {
        if (chart == null) {
            return;
        }

        chart.setDescription("");
        chart.setNoDataTextDescription("You need to provide data for the chart.");

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);

        chart.setData(new LineData());

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setSpaceBetweenLabels(0);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(MAX_Y_VALUE);
        leftAxis.setAxisMinValue(MIN_Y_VALUE);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }
}
