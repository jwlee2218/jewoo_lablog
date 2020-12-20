package com.example.blue_poten;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {
    private LineChart chart;
    private BluetoothSPP bt;
    private List<Float> pot_values = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pot_values.add(0f);
        bt = new BluetoothSPP(this); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            TextView pot = findViewById(R.id.pot);

            public void onDataReceived(byte[] data, String message) {
                String[] array  = message.split(",");
                pot_values.add(Float.parseFloat(array[0]));
                pot.setText(array[0]);
//                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = findViewById(R.id.btnConnect); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

//      CHart Drawing

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDescription("");
        chart.setNoDataTextDescription("No data for the moment");
        chart.setHighlightPerDragEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);

        YAxis yl = chart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        yl.setAxisMaxValue(1024f);
        yl.setDrawGridLines(true);

        YAxis yl2 = chart.getAxisRight();
        yl2.setEnabled(false);
    }
    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {

            @Override
            public void run(){
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }

                    });

                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {

                    }

                }
            }
        }).start();
    }

    private void addEntry() {
        LineData data = chart.getData();
        float random_float = pot_values.size();
//        pot_values.add(0f);

        if (data != null) {
            LineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addXValue("");
            data.addEntry(new Entry(pot_values.get(pot_values.size()-1), set.getEntryCount()), 0);

            // OG
//            data.addEntry(new Entry((float) (Math.random() * 120)+ 5f, set.getEntryCount()), 0);
            // Tried
//            data.addEntry(new Entry(pot_values.get(pot_values.size()-1), set.getEntryCount()), 0);
//            data.addEntry(new Entry(random_float, set.getEntryCount()), 0);

            chart.notifyDataSetChanged();

            chart.setVisibleXRange(0f,6f);
            chart.moveViewToX(data.getXValCount()-7);
        }
    }

//    private void addEntry() {
//        LineData data = chart.getData();
//        bt = new BluetoothSPP(this); //Initializing
//
//        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
//            Toast.makeText(getApplicationContext()
//                    , "Bluetooth is not available"
//                    , Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        final ArrayList<String> pot_values = new ArrayList<>();
//        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
//            TextView pot = findViewById(R.id.pot);
//
//            public void onDataReceived(byte[] data, String message) {
//                String[] array  = message.split(",");
//                pot.setText(array[0]);
//                pot_values.add(array[0]);
//
////                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//            }
//        });
//        if (data != null) {
//            LineDataSet set = data.getDataSetByIndex(0);
//
//            if (set == null) {
//                set = createSet();
//                data.addDataSet(set);
//            }
//            data.addXValue("");
//            data.addEntry(new Entry(Float.parseFloat(pot_values.get(0)), set.getEntryCount()), 0);
//
//            chart.notifyDataSetChanged();
//
//            chart.setVisibleXRange(0f,6f);
//            chart.moveViewToX(data.getXValCount()-7);
//        }
//    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "SPL Db");
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 177));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);

        return set;
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend = findViewById(R.id.btnSend); //데이터 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("Text", true);
            }
        });
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



}
