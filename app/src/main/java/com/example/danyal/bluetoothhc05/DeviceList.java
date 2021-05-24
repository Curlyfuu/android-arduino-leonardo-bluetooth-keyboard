package com.example.danyal.bluetoothhc05;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    Button btnPaired;
//    TextView textView_label;
    ListView devicelist;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private int torC = 0;//触控板或者ppt模式
    public static String EXTRA_ADDRESS = "device_address";
    Button btn_tpad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        btnPaired = (Button) findViewById(R.id.button);
        devicelist = (ListView) findViewById(R.id.listView);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
            finish();
        } else if (!myBluetooth.isEnabled()) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
        btn_tpad = (Button) findViewById(R.id.button_tpad);
//        textView_label = (TextView) findViewById(R.id.textview_label);
        btn_tpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                torC += 1;
                if(torC>2){
                    torC=0;
                }if(torC==0){
                    btn_tpad.setText("PPT");
                }else if(torC==1){
                    btn_tpad.setText("TPA");
                }else{
                    btn_tpad.setText("GYC");
                }
            }
        });

    }

    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName().toString() + "\n" + bt.getAddress().toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener);
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            if(torC==1){
                Intent i = new Intent(DeviceList.this, TestActivity.class);
                i.putExtra(EXTRA_ADDRESS, address);
                startActivity(i);
            } else if(torC==0){
                Intent i = new Intent(DeviceList.this, PPTControlActivity.class);
                i.putExtra(EXTRA_ADDRESS, address);
                startActivity(i);
            }else{
                Intent i = new Intent(DeviceList.this, GyroControlActivity.class);
                i.putExtra(EXTRA_ADDRESS, address);
                startActivity(i);
            }

        }
    };
}
