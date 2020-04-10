package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.Set;

public class multiplayerConnect extends AppCompatActivity {

    InputStream inStream;
    OutputStream outputStream;
    private static final int REQUEST_ENABLE_BT = 1;

    public void pairDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Object[] devices = pairedDevices.toArray();
            BluetoothDevice device = (BluetoothDevice) devices[0];
            ParcelUuid[] uuid = device.getUuids();
            try {
                BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid[0].getUuid());
                socket.connect();
                Toast.makeText(this, "Socket connected", Toast.LENGTH_LONG).show();
                outputStream = socket.getOutputStream();
                inStream = socket.getInputStream();
            } catch (IOException e) {
                Toast.makeText(this, "Exception found"+e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }


    public void SendMessage() {
        EditText outMessage = (EditText) findViewById(R.id.editText2);
        try {
            if (outputStream != null)
                outputStream.write(/*outMessage.toString()*/1);
            /*TextView displayMessage = (TextView) findViewById(R.id.textView2);
            Scanner s = new Scanner(inStream).useDelimiter("\\A");
            displayMessage.setText(s.hasNext() ? s.next() : "");*/
        } catch (IOException e) {/*Do nothing*/}
        Toast.makeText(this, "No output stream", Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_bluetooth);
        pairDevice();

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
    }
}