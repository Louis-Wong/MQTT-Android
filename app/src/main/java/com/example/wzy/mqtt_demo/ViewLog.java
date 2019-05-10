package com.example.wzy.mqtt_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewLog extends AppCompatActivity {

    private ListView listLog;
    private static ArrayList<String> list = new ArrayList<String>();
    private static BaseAdapter adapter;
    private Button clearBnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log);
        init();
        onClear();
    }

    private void init(){
        listLog = (ListView)findViewById(R.id.listLog);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listLog.setAdapter(adapter);
        clearBnt = (Button)findViewById(R.id.clearBnt);
    }

    public static ArrayList<String> getList(){
        return list;
    }

    public static BaseAdapter getAdapter(){
        return adapter;
    }

    private void onClear(){
        clearBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

}
