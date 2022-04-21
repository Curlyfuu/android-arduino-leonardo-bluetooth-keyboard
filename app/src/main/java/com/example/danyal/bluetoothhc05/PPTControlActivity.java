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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class
PPTControlActivity extends AppCompatActivity implements SensorEventListener {

    private static final int WINDOW_SIZE = 20;
    private static final double[] X = {0, 1, 2, 3, 4, 5};
    private static final String TAG = "HELLO";
    public static String EXTRA_ADDRESS = "device_address";


    Button btn_esc, btn_head, btn_now, btn_win_left, btn_win_right, btn_up, btn_down, btn_left, btn_right, btn_fmouse, btn_lazer;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private boolean send_flag = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean mRegister1;
    private boolean mRegister2;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticSensor;
    private Sensor mOra;
    private float old_x = 0;
    private float old_y = 0;

    SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        setContentView(R.layout.activity_ppt_control);
        btn_esc = (Button) findViewById(R.id.button_esc);
        btn_now = (Button) findViewById(R.id.button_now);

        btn_head = (Button) findViewById(R.id.button_head);
        btn_win_left = (Button) findViewById(R.id.button_win_l);
        btn_win_right = (Button) findViewById(R.id.button_win_r);
        btn_up = (Button) findViewById(R.id.button_up);
        btn_down = (Button) findViewById(R.id.button_down);
        btn_left = (Button) findViewById(R.id.button_left);
        btn_right = (Button) findViewById(R.id.button_right);
        btn_fmouse = (Button) findViewById(R.id.button_flymouse);
        btn_lazer = (Button) findViewById(R.id.button_lazer);


        new ConnectBT().execute();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        mOra = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

//        mKalman = new Kalman();

        btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Cn\n");
            }
        });
        btn_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Ch\n");
            }
        });
        btn_win_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*C4\n");
            }
        });
        btn_win_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*C6\n");
            }
        });
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Cu\n");
            }
        });
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Cd\n");
            }
        });
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Cl\n");
            }
        });
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Cr\n");
            }
        });
        btn_esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Ce\n");
            }
        });
        btn_lazer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Cg\n");
            }
        });
        btn_fmouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_flag = !send_flag;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mSensorManager.registerListener(this, mOra, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (send_flag) {
            float[] R = new float[16];
            float[] orientationValues = new float[3];
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) { // compass
                orientationValues[0] = sensorEvent.values[0];
                orientationValues[1] = sensorEvent.values[1];
                orientationValues[2] = sensorEvent.values[2];

                sendSignal("*x" + (int) (orientationValues[0] * 30.0) + "y" + (int) (orientationValues[1] * 30.0) + "\n");


            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(PPTControlActivity.this, "Connecting...", "Please Wait!!!");
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

    @Override
    protected void onPause() {
        super.onPause();
        Disconnect();

    }

}
