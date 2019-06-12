package com.example.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    public static ArrayList<String> arrayList;
    public static ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);
        try{
            arrayList = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("arrayList",ObjectSerializer.serialize(new ArrayList<String>())));
            ArrayList<String> latitudes = new ArrayList<>();
            ArrayList<String> longitudes = new ArrayList<>();
            latitudes = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("Array:",arrayList.toString());
            if(latitudes.size() == longitudes.size() && latitudes.size() > 0){
                for (int i=0;i<latitudes.size();i++){
                    MapsActivity.latLng.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                }
                Log.i("LAtLOnG",MapsActivity.latLng.toString());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if(!arrayList.contains("Add Item"))
            arrayList.add("Add Item");
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("Position",position);
                intent.putExtra("ArrayList",arrayList);
                startActivity(intent);
            }
        });

    }
}
