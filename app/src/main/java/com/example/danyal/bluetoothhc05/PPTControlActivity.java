package com.example.danyal.bluetoothhc05;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
PPTControlActivity extends AppCompatActivity {

//    private static final int TIMER = 1;
//    private static final int WINDOW_SIZE_AVG = 4;
    private static final int WINDOW_SIZE = 20;
    private static final double[] X = {0, 1, 2, 3, 4, 5};
    private static final String TAG = "HELLO";
    public static String EXTRA_ADDRESS = "device_address";


    Button btn_alt,btn_tab,btn_esc, btn_head, btn_now, btn_win_left, btn_win_right, btn_up, btn_down, btn_left, btn_right, btnDis;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private boolean alt_status = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private float[] values, r, gravity, geomagnetic;
//    double[] lastData = new double[2];
//    int xyIndex = 0;
//    boolean initaled = false;
//    boolean send_flag = false;
//    boolean xyturn = false;
//    String old_x_dist = "";
//    String  old_y_dist = "";
    double[] arrx = new double[WINDOW_SIZE];
    double[] arry = new double[WINDOW_SIZE];

    SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化数组
//        values = new float[3];//用来保存最终的结果
//        gravity = new float[3];//用来保存加速度传感器的值
//        r = new float[9];//
//        geomagnetic = new float[3];//用来保存地磁传感器的值


        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        setContentView(R.layout.activity_ppt_control);
        btn_esc = (Button) findViewById(R.id.button_esc);
        btn_now = (Button) findViewById(R.id.button_now);
        btn_alt = (Button) findViewById(R.id.button_alt);
        btn_tab = (Button) findViewById(R.id.button_tab);

        btn_head = (Button) findViewById(R.id.button_head);
        btn_win_left = (Button) findViewById(R.id.button_win_l);
        btn_win_right = (Button) findViewById(R.id.button_win_r);
        btn_up = (Button) findViewById(R.id.button_up);
        btn_down = (Button) findViewById(R.id.button_down);
        btn_left = (Button) findViewById(R.id.button_left);
        btn_right = (Button) findViewById(R.id.button_right);
        btnDis = (Button) findViewById(R.id.button_dis);
//        btn_vm =(Button) findViewById(R.id.button_vm);
//        btn_vp =(Button) findViewById(R.id.button_vp);


        new ConnectBT().execute();

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
        btn_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("*Ct\n");
            }
        });
        btn_alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alt_status=!alt_status;
                if(alt_status){
                    btn_alt.setText("ALT ON");
                    sendSignal("*Cs\n");
                }else {
                    btn_alt.setText("ALT OFF");
                    sendSignal("*Cg\n");
                }

            }
        });
        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), sensorManager.SENSOR_DELAY_GAME);
//        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_GAME);
//    }

//    SensorEventListener sensorEventListener = new SensorEventListener() {
//
//        @Override
//        public void onSensorChanged(SensorEvent sensorEvent) {
//
//            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//                geomagnetic = sensorEvent.values;
//            }
//
//            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                gravity = sensorEvent.values;
//                SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
//                SensorManager.getOrientation(r, values);
//                double azimuth = Math.toDegrees(values[0]);
//                double x_lateral = azimuth;
//
//
//                double y_lateral = Math.toDegrees(values[1]);
//                if (!initaled) {
//                    lastData[0] = x_lateral;
//                    lastData[1] = y_lateral;
//                    initaled = true;
//                } else {
//                    arrx[xyIndex] = x_lateral;
//                    arry[xyIndex] = y_lateral;
//                    if (xyIndex < WINDOW_SIZE - 1) {
//                        xyIndex++;
//                    } else {
//                        xyIndex = 0;
//                    }
////                    Log.i(TAG, Arrays.toString(arrx));
////                    String x_dist = Integer.toString((int)(3*avgValue(seqNum(arrx, xyIndex-1, 10))));
////                    String y_dist = Integer.toString((int)avgValue(seqNum(arry, xyIndex-1, 10)));
////                    String y_dist = Integer.toString((int) ((arry[0] + arry[1] + arry[2]+ arry[3] + arry[4] + arry[5]) / 3));
////                    String x_dist = Integer.toString((int)(10.0*x_lateral-10.0*lastData[0]));
////                    String y_dist = Integer.toString((int)(5.0*y_lateral-5.0*lastData[1]));
////                    double[] x_paras= aandB(X,seqNum(arrx,xyIndex-1,WINDOW_SIZE));
////                    String x_dist = Integer.toString((int)((x_paras[1]*WINDOW_SIZE+x_paras[0])/4));
////                    double[] y_paras= aandB(X,seqNum(arry,xyIndex-1,WINDOW_SIZE));
////                    String y_dist = Integer.toString((int)((y_paras[1]*WINDOW_SIZE+y_paras[0])/4));
//
//
//
//
////                    TextView textViewx = (TextView) findViewById(R.id.text_x);
////                    TextView textViewy = (TextView) findViewById(R.id.text_y);
////
////                    textViewx.setText(Double.toString(x_lateral));
////                    textViewy.setText(Double.toString(y_lateral));
//
////                    if (send_flag) {
////                        String x_dist = Double.toString(Math.round(avgValue(arrx)*2));
////
////                        String y_dist = Double.toString(Math.round(avgValue(arry)*2));
////
////                        if (xyturn) {
////                            if (varValue(arrx) > 0.05) {
////                                sendSignal("*x" + x_dist + "\n");
//////                                Log.i(TAG, x_dist);
////                            } else {
////                                sendSignal("*x" + old_x_dist + "\n");
////                                old_x_dist = x_dist;
////                            }
////                            xyturn = !xyturn;
////                        } else {
////                            if (varValue(arry) > 0.05) {
////                                sendSignal("*y" + y_dist + "\n");
////                            }else{
////                                sendSignal("*y" + old_y_dist + "\n");
////                                old_y_dist = y_dist;
////                            }
////                            xyturn = !xyturn;
////                        }
//
//
////                        sendSignal();
////                        Log.i(TAG, "x" + x_dist + "y" + y_dist + "\n");
//                    }
//                    lastData[0] = x_lateral;
//                    lastData[1] = y_lateral;
//                    testF kalmanfilter = new testF();
//                    kalmanfilter.initial();
//                    double[] temp = seqNum(arrx,xyIndex-1,WINDOW_SIZE);
//                    ArrayList<Integer> list = new ArrayList<Integer>();
//                    for (int i = 0; i < arrx.length; i++) {
//                        list.add((int)(arrx[i]*100));
//                    }
//                    int oldvalue = list.get(0);
//                    ArrayList<Integer> alist = new ArrayList<Integer>();
//                    for (int i = 0; i < list.size(); i++) {
//                        int value = list.get(i);
//                        oldvalue = kalmanfilter.KalmanFilter(oldvalue, value);
//                        alist.add(oldvalue);
//                    }
//
//
//                    Log.i(TAG, "**"+list.toString());
//                    Log.i(TAG, "*"+alist.toString());
//
//
//                }
//
//
////                double azimuth = Math.toDegrees(values[0]);
////                double x_lateral = azimuth;
////                double y_lateral = Math.toDegrees(values[1]);
////                double z_lateral = Math.toDegrees(values[2]);
////                String Yaw = Double.toString(x_lateral);
////                String Pitch = Double.toString(y_lateral);
////                String Rol = Double.toString(z_lateral);
////
////                sendSignal("R"+Yaw.substring(0,3)+"\n");
////                sendSignal("P"+Pitch.substring(0,4)+"\n");
////                sendSignal("Y"+Rol.substring(0,4)+"\n");
////                textViewx.setText("Yaw" + Yaw.substring(0,3));
////                textViewy.setText("Pitch" + Pitch.substring(0,4));
////                textViewz.setText("Rol" + Rol.substring(0,4));
////            }
////            Log.i(TAG, new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
//
//
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int i) {
//
//        }
//    };

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
    //    public double avgValue(double[] a) {
//        double avg = 0.0;
//        for (int i = 0; i < a.length; i++) {
//            avg += a[i] / a.length;
//        }
//        return avg;
//    }
//
//    public double varValue(double[] a) {
//        double avg = avgValue(a);
//        double avr = 0.0;
//        for (int i = 0; i < a.length; i++) {
//            avr += (a[i] - avg) * (a[i] - avg);
//        }
//        return avr;
//    }
//
//    public double[] seqNum(double[] a, int index, int num) {
//        double[] result = new double[num];
//        if (index >= num - 1) {
//
//            for (int i = 0; i < num; i++) {
//                result[i] = a[i + index - num + 1];
//
//            }
//            return result;
//        } else if (index < num - 1) {
//            for (int i = 0; i <= index; i++) {
//                result[(num - index - 1) + i] = a[i];
//            }
//            for (int i = 0; i < num - index - 1; i++) {
//                result[i] = a[a.length - (num - index - 1) + i];
//            }
//            return result;
//        } else {
//            return result;
//        }
//    }
//
//    public double[] aandB(double[] x, double[] y) {
//        Queint queint = new Queint(x, y, 2);
//        return queint.getCoefficient();
//    }
}
