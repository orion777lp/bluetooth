package com.example.bluetooth;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class main extends Activity {

    /*
    * get_status
    * set_alaram_on
    * set_alaram_off
    * set_alaram
    * set_time
    * set_led_on
    * set_led_off
    *
    * */

    private String get_status = "100";
    private String set_alaram_on= "201";
    private String set_alaram_off = "202";
    private String set_alaram = "203";
    private String set_time = "204";
    private String set_led_on = "205";
    private String set_led_off = "206";

    private Button btn_get, btn_on_alarm, btn_set_alaram, btn_set_time, btn_on_led;
    private TimePicker timePicker;
    private Spinner work_day, work_time;
    private CheckBox checkBox_monday, checkBox_tuesday, checkBox_wednesday, checkBox_thursday, checkBox_friday, checkBox_saturday, checkBox_sunday;
    private TextView text_alarm, text_led, text_info;

    private boolean led_on = false;
    private boolean alaram_on = false;
    private boolean in_status = false;
    private boolean end_msg = false;

    private String income = "";

    Spinner spinner;
    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm);

        findElement();

        setAction();

        timePicker.setIs24HourView(true);

        spinner = (Spinner) findViewById(R.id.day_work);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //textView.setText(spinner.getSelectedItem().toString());
                //textView.dismissDropDown();
                //Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //textView.setText(spinner.getSelectedItem().toString());
                //textView.dismissDropDown();
                //Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
            }
        });

        //Link the buttons and textViews to respective views
        /*
        btnOn = (Button) findViewById(R.id.buttonOn);
        btnOff = (Button) findViewById(R.id.buttonOff);

        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);

        sensorView0 = (TextView) findViewById(R.id.sensorView0);
        sensorView1 = (TextView) findViewById(R.id.sensorView1);
        sensorView2 = (TextView) findViewById(R.id.sensorView2);
        sensorView3 = (TextView) findViewById(R.id.sensorView3);
*/
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                          // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);

                    if(readMessage.charAt(0) == '#'){
                        text_info.setText("");
                        income = "";
                    }else if(readMessage.charAt(0) == '!'){
                        text_info.setText(""+readMessage);
                        income = ""+readMessage;
                        in_status = true;
                    }else{
                        income = income + readMessage;
                        text_info.setText(income);
                    }

                    if(in_status){

                            if(income.substring(income.length()-1).equals("~")){

                                String ready = income.substring(1, income.length()-1);
                                String []param = ready.split(";");
                                text_info.setText(ready);

                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                                Date date = null;
                                try
                                {
                                    date = sdf.parse(param[0]+":"+param[1]);
                                }catch (ParseException e){

                                }

                                Calendar c = Calendar.getInstance();
                                c.setTime(date);

                                timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                                timePicker.setCurrentMinute(c.get(Calendar.MINUTE));

                                in_status = false;
                            }


                       // Toast.makeText(getApplicationContext(), income, Toast.LENGTH_SHORT).show();
                        /*
                        long dv = Long.valueOf(timestamp_in_string)*1000;// its need to be in milisecond
                        Date df = new java.util.Date(dv);
                        String vv = new SimpleDateFormat("hh:mm").format(df);

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                        Date date = null;
                        try {
                            date = sdf.parse("13:45");
                        } catch (ParseException e) {
                        }
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);


                        timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
                   */
                        end_msg = false;
                    }

                                      // determine the end-of-line
                    /*
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                     //   txtString.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                    //    txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                       // text_info.setText( readMessage);

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);
/*
                            sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");    //update the textviews with sensor values
                            sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
                            sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
                            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                    */
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter

        checkBTState();
/*
        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("0");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });*/

        findViewById(R.id.button_select_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Oops => "+e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void findElement(){
        btn_get = findViewById(R.id.button_get);
        btn_on_alarm = findViewById(R.id.button_on);
        btn_set_alaram = findViewById(R.id.button_set);
        btn_set_time = findViewById(R.id.button_set_time);
        btn_on_led = findViewById(R.id.button_on_led);
        timePicker = findViewById(R.id.timePicker);
        work_day = findViewById(R.id.day_work);
        work_time = findViewById(R.id.time_work);
        checkBox_monday = findViewById(R.id.checkBox_monday);
        checkBox_tuesday = findViewById(R.id.checkBox_tuesday);
        checkBox_wednesday = findViewById(R.id.checkBox_wednesday);
        checkBox_thursday = findViewById(R.id.checkBox_thursday);
        checkBox_friday = findViewById(R.id.checkBox_friday);
        checkBox_saturday = findViewById(R.id.checkBox_saturday);
        checkBox_sunday = findViewById(R.id.checkBox_sunday);
        text_alarm = findViewById(R.id.textView_on_alarm);
        text_led = findViewById(R.id.textView_led_status);
        text_info = findViewById(R.id.textView_info);
    }

    public void setAction(){

        btn_get.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), get_status, Toast.LENGTH_LONG).show();
                mConnectedThread.write(get_status);
            }
        });

        btn_on_alarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write(set_alaram_on);
            }
        });

        btn_set_alaram.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), set_alaram, Toast.LENGTH_LONG).show();
                mConnectedThread.write(set_alaram);
            }
        });

        btn_set_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), set_time, Toast.LENGTH_LONG).show();
                mConnectedThread.write(set_time);
            }
        });

        btn_on_led.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), set_led_on, Toast.LENGTH_LONG).show();
                mConnectedThread.write(set_led_on);
            }
        });

    }

}
