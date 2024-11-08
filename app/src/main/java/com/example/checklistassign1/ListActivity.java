package com.example.checklistassign1;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {
    private Button btnback;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        String FN = "history.json";

        btnback = findViewById(R.id.backbutton);
        final ListView listView = (ListView)findViewById(R.id.listView);

        String data = null ;
        FileInputStream inputStream;
        try {
            File file = new File(getExternalFilesDir(null), FN);
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputStream.read(buffer);
            inputStream.close();
            data = new String(buffer, "UTF-8");
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            JSONArray dataArray = new JSONArray(data);
            ArrayList<HashMap<String, String>> list = new ArrayList<>();
            for (int i = dataArray.length() - 1; i >= 0; i--) {
                JSONObject jsonObject = dataArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("date", jsonObject.getString("date"));
                map.put("weight", String.valueOf(jsonObject.getInt("weight")));
                map.put("bmiValue", jsonObject.getString("bmiValue"));
                map.put("bmiStatus", jsonObject.getString("bmiStatus"));

                list.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(ListActivity.this, list, R.layout.activity_column,
                    new String[]{"date", "weight", "bmiValue", "bmiStatus"},
                    new int[]{R.id.dateList, R.id.weightList, R.id.bmiList, R.id.statusList});

            listView.setAdapter(simpleAdapter);
        } catch(Exception e) {
            e.printStackTrace();
        }


        btnback.setOnClickListener(view ->{
            Intent BB = new Intent(ListActivity.this, MainActivity.class);
            startActivity(BB);
        });

    }
}
