package com.example.danyal.bluetoothhc05;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class TestActivity extends AppCompatActivity implements SensorEventListener {
    TestView view;
    private static final String TAG = "HELLO";
    private static final int WINDOW_SIZE = 5;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private float cur_x, cur_y;     //当前座标
    private float cur_x_old, cur_y_old;     //当前座标
    private float downX, downY;     //手指按下的座标
    private float endX, endY;       //手指抬起座标
    private float detaX, detaY;     //座标增量
    boolean send_flag = false;
    private float[] values, r, gravity, geomagnetic;
    SensorManager sensorManager;
    private boolean mRegister1;
    private boolean mRegister2;
    private boolean mRegister3;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticSensor;
    private Sensor mOra;

    private float[] aValues = new float[3];
    private float[] mValues = new float[3];


//    private Kalman mKalman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        values = new float[3];//用来保存最终的结果
        gravity = new float[3];//用来保存加速度传感器的值
        r = new float[9];//
        geomagnetic = new float[3];//用来保存地磁传感器的值


        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        new ConnectBT().execute();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mOra = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

//        mKalman = new Kalman();

//        findViewById(R.id.button_lb).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendSignal("*C1\n");
//            }
//        });
        findViewById(R.id.button_xlb).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.button_xlb) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendSignal("*C^\n");
                        send_flag=false;
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendSignal("*Cv\n");
                        send_flag=true;
                    }
                }
                return false;
            }

        });
        findViewById(R.id.button_xup).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.button_xup) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendSignal("*C:\n");
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendSignal("*C;\n");
                    }
                }
                return false;
            }

        });
        findViewById(R.id.button_xdown).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.button_xdown) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendSignal("*C>\n");
//                        send_flag = false;
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendSignal("*C.\n");
//                        send_flag = true;
                    }
                }
                return false;
            }

        });
        findViewById(R.id.button_xra).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.button_xra) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendSignal("*C?\n");
                        send_flag = !send_flag;
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendSignal("*C/\n");
//                        send_flag = true;
                    }
                }
                return false;
            }

        });
        findViewById(R.id.button_xla).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.button_xla) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendSignal("*C<\n");
                        send_flag = !send_flag;
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendSignal("*C,\n");
//                        send_flag = true;
                    }
                }
                return false;
            }

        });
        findViewById(R.id.button_xe).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.button_xe) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendSignal("*C}\n");
//                        send_flag = !send_flag;
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendSignal("*C]\n");
//                        send_flag = true;
                    }
                }
                return false;
            }

        });


        view = (TestView) findViewById(R.id.testxView);

        view.setTouchListener(new TouchEvent());
//        Button btnClearResult = (Button)findViewById(R.id.btn_clear_result);
//        Button btnClearTest = (Button)findViewById(R.id.btn_clear_test);

//        EventClick eventClick = new EventClick();

//        btnClearResult.setOnClickListener(eventClick);
//        btnClearTest.setOnClickListener(eventClick);
    }


    //    private class EventClick implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()){
//                case R.id.btn_clear_result:
////                    paintView.reset();
//                    break;
//
//                case R.id.btn_clear_test:
//                    view.reset();
//                    break;
//            }
//        }
//    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mRegister1 = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), sensorManager.SENSOR_DELAY_UI);
//        mRegister2 = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_UI);
        mRegister1 = mSensorManager.registerListener(this,mAccelerometerSensor,SensorManager.SENSOR_DELAY_GAME);
        mRegister2= mSensorManager.registerListener(this,mMagneticSensor,SensorManager.SENSOR_DELAY_GAME);
        mRegister3 = mSensorManager.registerListener(this,mOra,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] R = new float[16];
        float[] orientationValues = new float[3];
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) { // compass
            orientationValues[0] = sensorEvent.values[0];
            orientationValues[1] = sensorEvent.values[1];
            orientationValues[2] = sensorEvent.values[2];



//            orientationValues[0] = (float) Math.toDegrees(orientationValues[0]);
//            orientationValues[1] = (float) Math.toDegrees(orientationValues[1]);
//            orientationValues[2] = (float) Math.toDegrees(orientationValues[2]);

//            textView1.setText("YAW:" + Float.toString(X_lateral).substring(0,5));
//            textView2.setText("PIT:" + Float.toString(Y_longitudinal).substring(0,5));
//            textView3.setText("ROL:" + Float.toString(Z_vertical).substring(0,5));
            if (send_flag) {
//            SensorSingleData data = mKalman.filter(new SensorSingleData(orientationValues[1], orientationValues[2], orientationValues[0]));
//            Log.i(TAG,"*x" + (int) (orientationValues[0] * 10.0) + "y" + (int) (orientationValues[2] * 10.0) + "\n");
            sendSignal("*x" + (int) (orientationValues[0] * 15.0) + "y" + (int) (-orientationValues[2] * 15.0) + "\n");
//            sendSignal("*x" + (int) (data.getAccX() * 40.0) + "y" + (int) (data.getAccZ() * 40.0) + "\n");
        }
        }

    }

//        switch (sensorEvent.sensor.getType()) {
//            case Sensor.TYPE_ACCELEROMETER:
//                aValues = sensorEvent.values.clone();
//                break;
//            case Sensor.TYPE_MAGNETIC_FIELD:
//                mValues = sensorEvent.values.clone();
//                break;
//        }

//        float[] R = new float[16];
//        float[] orientationValues = new float[3];
//
//        SensorManager.getRotationMatrix(R, null, aValues, mValues);
//        SensorManager.getOrientation(R, orientationValues);
//
//        orientationValues[0] = (float) Math.toDegrees(orientationValues[0]);
//        orientationValues[1] = (float) Math.toDegrees(orientationValues[1]);
//        orientationValues[2] = (float) Math.toDegrees(orientationValues[2]);


//        String originTxt = "(pitch)x:"+orientationValues[1]+
//                "\n(roll)y:"+orientationValues[2]+
//                "\n(azimuth)z:"+orientationValues[0];
//
//        String showTxt = "(pitch)x:"+data.getAccX()+
//                "\n(roll)y:"+data.getAccY()+
//                "\n(azimuth)z:"+data.getAccZ();
//        mTvOrigin.setText(originTxt);
//        mTvShow.setText(showTxt);
//

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public double[] seqNum(double[] a, int index, int num) {
        double[] result = new double[num];
        if (index <= num - 1 && index >= 0) {
            for (int i = 0; i <= index; i++) {
                result[(num - index - 1) + i] = a[i];
            }
            for (int i = 0; i < num - index - 1; i++) {
                result[i] = a[index + i + 1];
            }
            return result;
        } else {
            for (int i = 0; i < num - 1; i++) {
                result[i] = a[i];

            }
            return result;
        }
    }


    private class TouchEvent implements TouchListener {

        @Override
        public void onTouchDown(int x, int y) {
            downX = x;
            downY = y;
            //计算偏移矢量
            detaX += endX - downX;
            detaY += endY - downY;
//            paintView.setTouchDown(x,y);
//            if (detaX < 4 && detaY < 4) {
//                sendSignal("*C1");
//            }
        }

        @Override
        public void onTouchMove(int x, int y) {
//            float tmpx = x + detaX;


//            Log.i(TAG,"*x" + (int) (cur_x) + "y" + (int) (cur_y) + "\n");

//            paintView.setTouchMove(x,y);
            send_flag=false;
            cur_x = x + detaX;
            cur_y = y + detaY;
            sendSignal("*x" + (int) (cur_x) + "y" + (int) (cur_y) + "\n");
        }

        @Override
        public void onTouchUp(int x, int y) {
            endX = x;
            endY = y;
//            arrx[xyindex] = abs((int) (cur_y - cur_y_old)) + abs((int) (cur_x - cur_x_old));
//            xyindex++;
//            if (xyindex > 7) {
//                xyindex = 0;
//            }
//            double sum = 0;
//            for (int i = 0; i < arrx.length; i++) {
//                sum+=arrx[i];
//            }

//            if (abs((int) (cur_y - cur_y_old)) + abs((int) (cur_x - cur_x_old)) < 15.0) {
//                sendSignal("*Cm\n");
//                Log.i(TAG, abs((int) (cur_y - cur_y_old)) + abs((int) (cur_x - cur_x_old))+"done");
//            }
//            Log.i(TAG, abs((int) (cur_y - cur_y_old)) + abs((int) (cur_x - cur_x_old))+" ");
            cur_y_old = cur_y;
            cur_x_old = cur_x;
//            paintView.setTouchUp(x,y);
        }
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(TestActivity.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }

    private void sendSignal(String ss) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(ss.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Disconnect();
        super.onPause();
        if (mRegister1 || mRegister2) {
            sensorManager.unregisterListener((SensorEventListener) this);
        }

    }

}

