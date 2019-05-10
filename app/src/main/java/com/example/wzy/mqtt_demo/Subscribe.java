package com.example.wzy.mqtt_demo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.*;
import android.graphics.Color;

public class Subscribe extends AppCompatActivity {

    private TextView subLabel;
    private EditText editSub;
    private Button subBnt;
    private Button unsunBnt;
    private ListView subMess;
    private static ArrayList<String> list = new ArrayList<String>();
    private static BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        init();
        onClickSub();
        onClickUnsub();
    }

    private void init(){
        subLabel = (TextView)findViewById(R.id.subLabel);
        editSub = (EditText)findViewById(R.id.editSub);
        subBnt = (Button)findViewById(R.id.subscribeBnt);
        unsunBnt = (Button)findViewById(R.id.unsubscribeBnt);
        subMess = (ListView)findViewById(R.id.subMess);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        subMess.setAdapter(adapter);
    }

    public void onClickSub(){
        subBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Publish.getMqttClient().isConnected()) {
                        final EditText editSub = (EditText) findViewById(R.id.editSub);
                        try {
                            Publish.getMqttClient().subscribe(editSub.getText().toString().trim());
                            Toast.makeText(Subscribe.this, "Subscribe successfully", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            Toast.makeText(Subscribe.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                            Publish.stateText.setText("Disconnected");
                            Publish.stateText.setTextColor(Color.RED);
                        }
                    } else {
                        Toast.makeText(Subscribe.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                        Publish.stateText.setText("Disconnected");
                        Publish.stateText.setTextColor(Color.RED);
                    }
                }catch(Exception e){
                    Toast.makeText(Subscribe.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                    Publish.stateText.setText("Disconnected");
                    Publish.stateText.setTextColor(Color.RED);
                }
            }
        });
    }

    public void onClickUnsub(){
        unsunBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Publish.getMqttClient().isConnected()) {
                        final EditText editSub = (EditText) findViewById(R.id.editSub);
                        try {
                            Publish.getMqttClient().unsubscribe(editSub.getText().toString().trim());
                            Toast.makeText(Subscribe.this, "Unsubscribe successfully", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            Toast.makeText(Subscribe.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                            Publish.stateText.setText("Disconnected");
                            Publish.stateText.setTextColor(Color.RED);
                        }
                    } else {
                        Toast.makeText(Subscribe.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                        Publish.stateText.setText("Disconnected");
                        Publish.stateText.setTextColor(Color.RED);
                    }
                }catch(Exception e){
                    Toast.makeText(Subscribe.this, "Unavailable! Disconnected now!", Toast.LENGTH_SHORT).show();
                    Publish.stateText.setText("Disconnected");
                    Publish.stateText.setTextColor(Color.RED);
                }
            }
        });
    }

    public static ArrayList<String> getList(){
        return list;
    }

    public static BaseAdapter getAdapter(){
        return adapter;
    }

}
