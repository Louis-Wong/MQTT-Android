package com.example.wzy.mqtt_demo;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Intent;
import android.view.LayoutInflater;
import android.text.format.Time;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Publish extends AppCompatActivity implements SensorEventListener{

    private Time time;
    private Button conBnt;
    private Button pubBnt;
    private Button subBnt;
    private Button logBnt;
    private Button publishBnt;
    public static TextView stateText;
    private TextView xValue;
    private TextView yValue;
    private TextView zValue;
    private SensorManager sManager;
    private Sensor mSensorOrientation;

    private String host = "10.21.8.193";
    private String port = "61613";
    private String userName = "admin";
    private String password = "password";
    private String clientID = "Client";
    private String topic = "CS305/pro";
    private String TAG = "userTAG";

    private MqttConnectOptions mqttConOpt;
    private static MqttClient mqttClient;
    private MqttCallback mqttCallback;
    private Handler mqttHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        init();
        onChangeListener();
    }

    public static MqttClient getMqttClient(){
        return mqttClient;
    }

    private void init(){
        time = new Time();
        conBnt = (Button)findViewById(R.id.conBnt);
        pubBnt = (Button)findViewById(R.id.pubBnt);
        subBnt = (Button)findViewById(R.id.subBnt);
        logBnt = (Button)findViewById(R.id.logBnt);
        publishBnt = (Button)findViewById(R.id.publishBnt);
        stateText = (TextView) findViewById(R.id.stateText);
        xValue = (TextView) findViewById(R.id.sensorX);
        yValue = (TextView) findViewById(R.id.sensorY);
        zValue = (TextView) findViewById(R.id.sensorZ);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorOrientation = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sManager.registerListener(this, mSensorOrientation, SensorManager.SENSOR_DELAY_UI);
        mqttConOpt = new MqttConnectOptions();
        setConOpt();
        mqttHandler = new Handler();
        mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost (Throwable cause){
                try {
                    stateText.setText("Conn Lost");
                    stateText.setTextColor(Color.RED);
                }catch (Exception e){
                    stateText.setText("Conn Lost");
                    stateText.setTextColor(Color.RED);
                }
//                try {
//                    Log.d(TAG, "connection lost ");
//                    Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
//                    stateText.setText("Disconnected");
//                    stateText.setTextColor(Color.RED);
//                }catch(Exception e){
//                    Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
//                    stateText.setText("Disconnected");
//                    stateText.setTextColor(Color.RED);
//                }
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                String str = new String(mqttMessage.getPayload());
                Log.d(TAG,"message arrive: "+str);
                Message msg = new Message();
                msg.what = 3;
                msg.obj = str;
                mqttHandler.sendMessage(msg);
                Subscribe.getList().clear();
                Subscribe.getList().add("Received @ "+str);
                Subscribe.getAdapter().notifyDataSetChanged();
                time.setToNow();
                ViewLog.getList().add(ViewLog.getList().size()+" :Received @ "+str);
                ViewLog.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.d(TAG,"delivery complete ");
            }
        };
    }

    private void setConOpt(){
        mqttConOpt.setAutomaticReconnect(false);
        mqttConOpt.setCleanSession(true);
//        mqttConOpt.setUserName(userName);
//        mqttConOpt.setPassword(password.toCharArray());
        mqttConOpt.setMaxInflight(10);
        mqttConOpt.setConnectionTimeout(20);
        mqttConOpt.setKeepAliveInterval(60);
    }

    private void initClient(){
        try {
            mqttClient.setCallback(mqttCallback);
            mqttClient.connect(mqttConOpt);
            stateText.setText("Connected");
            stateText.setTextColor(Color.GREEN);
        } catch (MqttException e) {
            Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
            stateText.setText("Disconnected");
            stateText.setTextColor(Color.RED);
        }
    }

    private void sendMess(String msg){
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(msg.getBytes());
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        try {
            mqttClient.publish(topic,mqttMessage);
        } catch (MqttException e) {
            Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
            stateText.setText("Disconnected");
            stateText.setTextColor(Color.RED);
        }
    }

    private void disConn(){
        try {
            if(mqttClient != null) {
                mqttClient.disconnect();
                stateText.setText("Disconnected");
                stateText.setTextColor(Color.RED);
                mqttClient = null;
            }
        } catch (MqttException e) {
            Toast.makeText(Publish.this, "Unavailable operation!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onChangeListener(){
        onClickPublish();
        onClickSub();
        onClickLog();
        onClickCon();
        onClickPub();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xValue.setText("Orientation X：" + (float) (Math.round(event.values[0] * 100)) / 100);
        yValue.setText("Slope Y：         " + (float) (Math.round(event.values[1] * 100)) / 100);
        zValue.setText("Rolling Z：       " + (float) (Math.round(event.values[2] * 100)) / 100);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onClickSub(){
        subBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Publish.this,Subscribe.class);
                startActivity(intent);
            }
        });
    }

    public void onClickLog(){
        logBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Publish.this,ViewLog.class);
                startActivity(intent);
            }
        });
    }

    public void onClickPub(){
        pubBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Publish.this);
                builder.setTitle("Subject Setting");
                View view = LayoutInflater.from(Publish.this).inflate(R.layout.subdialog, null);
                builder.setView(view);

                final EditText sub = (EditText)view.findViewById(R.id.editSub);

                builder.setPositiveButton("Ensure", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try {
                            if (mqttClient.isConnected()) {
                                String subString = sub.getText().toString().trim();
                                topic = subString;
                                Toast.makeText(Publish.this, "Subject: " + subString + " has been published", Toast.LENGTH_SHORT).show();
                                try {
                                    MqttMessage mqttMessage = new MqttMessage();
                                    mqttMessage.setPayload("First connection".getBytes());
                                    mqttMessage.setQos(2);
                                    mqttMessage.setRetained(false);
                                    mqttClient.publish(topic, mqttMessage);
                                } catch (MqttException e) {
                                    Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                                    stateText.setText("Disconnected");
                                    stateText.setTextColor(Color.RED);
                                }
                            } else {
                                Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                                stateText.setText("Disconnected");
                                stateText.setTextColor(Color.RED);
                            }
                        }catch(Exception e){
                            Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                            Publish.stateText.setText("Disconnected");
                            Publish.stateText.setTextColor(Color.RED);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });
    }

    public void onClickPublish(){
        publishBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mqttClient.isConnected() == true) {
                        try {
                            time.setToNow();
                            String msg = time.hour + " h " + time.minute + " m " + time.second + " s " + " : " + "\n" + xValue.getText().toString() + "\n" + yValue.getText().toString() + "\n" + zValue.getText().toString() + "\n" + "at topic: " + topic;
                            sendMess(msg);
                            ViewLog.getList().add(ViewLog.getList().size() + " :Send @ " + msg);
                            ViewLog.getAdapter().notifyDataSetChanged();
                            Toast.makeText(Publish.this, "Publish successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(Publish.this, "Publish successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                        stateText.setText("Disconnected");
                        stateText.setTextColor(Color.RED);
                    }
                }catch (Exception e){
                    Toast.makeText(Publish.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                    stateText.setText("Disconnected");
                    stateText.setTextColor(Color.RED);
                }
            }
        });
    }

    public void onClickCon(){
        conBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Publish.this);
                builder.setTitle("Connection Setting");
                View view = LayoutInflater.from(Publish.this).inflate(R.layout.setdialog, null);
                builder.setView(view);

                final EditText username = (EditText)view.findViewById(R.id.editUser);
                final EditText addr = (EditText)view.findViewById(R.id.editAddr);
                final Button connBnt = (Button)view.findViewById(R.id.consetBnt);
                final Button disconBnt = (Button)view.findViewById(R.id.disConBnt);
                final EditText pass = (EditText)view.findViewById(R.id.editPassword);
                final EditText nick = (EditText)view.findViewById(R.id.editNick);

                connBnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mqttClient != null && mqttClient.isConnected()==false){
                            mqttClient = null;
                        }
                        try {
                            if(mqttClient == null && !username.getText().toString().trim().equals("") && !pass.getText().toString().trim().equals("")) {
                                String usernameString = username.getText().toString().trim();
                                String addrString = addr.getText().toString().trim();
                                String passString = pass.getText().toString().trim();
                                String nickNameString = nick.getText().toString().trim();
                                mqttConOpt.setUserName(usernameString);
                                mqttConOpt.setPassword(passString.toCharArray());
                                mqttClient = new MqttClient("tcp://" + addr.getText().toString().trim() + ":" + port, nickNameString, new MemoryPersistence());
                                initClient();
                                if(mqttClient.isConnected()) {
                                    Toast.makeText(Publish.this, "User: " + usernameString + " has connected to : " + addrString, Toast.LENGTH_SHORT).show();
                                }else{
                                    mqttClient=null;
                                }
                            }else if(mqttClient == null && !username.getText().toString().trim().equals("admin") && !pass.getText().toString().trim().equals("password")){
                                Toast.makeText(Publish.this, "Bad user name and password", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Publish.this, "You have already connected to the server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (MqttException e) {
                            Toast.makeText(Publish.this, "Bad user name and password", Toast.LENGTH_SHORT).show();
                            Publish.stateText.setText("Disconnected");
                            Publish.stateText.setTextColor(Color.RED);
                        }
                    }
                });
                disconBnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (mqttClient != null) {
                                disConn();
                                String usernameString = username.getText().toString().trim();
                                String addrString = addr.getText().toString().trim();
                                Toast.makeText(Publish.this, "User has cut off connection to : " + addrString, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Publish.this, "You are not connecting now!", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            Toast.makeText(Publish.this, "You are not connecting now!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

}
