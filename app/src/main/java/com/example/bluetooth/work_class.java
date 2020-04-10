package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

public class work_class extends AppCompatActivity {

    private BluetoothAdapter ba;
    private Set<BluetoothDevice> bd;

    private CheckBox enable_bt;
    private Button search_bt, connect_bt;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_layout);

        ba = BluetoothAdapter.getDefaultAdapter();

        if(ba == null){
            Toast.makeText(work_class.this, "Oops bluetooth not found, app is closed", Toast.LENGTH_SHORT).show();
            finish();
        }

        enable_bt = findViewById(R.id.enable_bt);
        search_bt = findViewById(R.id.search_bt);
        connect_bt = findViewById(R.id.connect_bt);
        listview = findViewById(R.id.list);

        if(ba.isEnabled()){
            enable_bt.setChecked(true);
        }

        enable_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    ba.disable();
                }else{
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 0 );
                }
            }
        });

        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(work_class.this, "Try show device", Toast.LENGTH_LONG).show();
                list();
            }
        });

        connect_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bluetoothScanning(getApplicationContext());

            }
        });

    }

    void bluetoothScanning(Context context){

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                ParcelUuid[] uuids = device.getUuids();
                String test =  getString(uuids.length);

                Log.i("Device Name: " , "device " + deviceName);
                Log.i("deviceHardwareAddress " , "hard"  + deviceHardwareAddress);
                Log.i("test " , "test"  + test);
            }
        }
    };

    private void list(){
        bd = ba.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice device : bd){
            list.add(device.getName());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

}
