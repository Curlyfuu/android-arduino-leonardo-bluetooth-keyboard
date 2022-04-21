package com.example.danyal.bluetoothhc05;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static java.lang.Math.abs;


public class TouchPadActivity extends AppCompatActivity {
    TestView view;
    private static final String TAG = "HELLO";
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
    double[] arrx = new double[3];
    double[] arry = new double[3];
    boolean xyturn = false;
    int xyindex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_pad);


        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        new ConnectBT().execute();

        findViewById(R.id.button_lb).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.button_lb) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendSignal("*C^\n");
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendSignal("*Cv\n");
                    }
                }
                return false;
            }

        });

        findViewById(R.id.button_middle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*C2\n");
            }
        });
        findViewById(R.id.button_rb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*C3\n");
            }
        });
        findViewById(R.id.button_mup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Cu\n");
            }
        });
        findViewById(R.id.button_mdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Cd\n");
            }
        });
        findViewById(R.id.button_mdis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });


        view = (TestView) findViewById(R.id.testView);

        view.setTouchListener(new TouchEvent());

    }


    private class TouchEvent implements TouchListener {

        @Override
        public void onTouchDown(int x, int y) {
            downX = x;
            downY = y;
            //计算偏移矢量
            detaX += endX - downX;
            detaY += endY - downY;

        }

        @Override
        public void onTouchMove(int x, int y) {

            cur_x = x + detaX;
            cur_y = y + detaY;
            sendSignal("*x" + (int) (cur_x) + "y" + (int) (cur_y) + "\n");
        }

        @Override
        public void onTouchUp(int x, int y) {
            endX = x;
            endY = y;


            if (abs((int) (cur_y - cur_y_old)) + abs((int) (cur_x - cur_x_old)) < 15.0) {
                sendSignal("*Cm\n");
                Log.i(TAG, abs((int) (cur_y - cur_y_old)) + abs((int) (cur_x - cur_x_old)) + "done");
            }
            Log.i(TAG, abs((int) (cur_y - cur_y_old)) + abs((int) (cur_x - cur_x_old)) + " ");
            cur_y_old = cur_y;
            cur_x_old = cur_x;
        }
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(TouchPadActivity.this, "Connecting...", "Please Wait!!!");
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

    }

}

