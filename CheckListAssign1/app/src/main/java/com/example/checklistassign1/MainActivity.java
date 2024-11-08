package com.example.checklistassign1;

import android.os.Bundle;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText etWeight, etHeight;
    private TextView tvBmi, tvBmiStatus, warnText;
    private Button btnCalculate;
    private ImageButton btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etWeight = findViewById(R.id.et_weight);
        etWeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(8, 2)}) ;
        etHeight = findViewById(R.id.et_height);
        etHeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(8, 2)}) ;
        tvBmi = findViewById(R.id.tv_bmi);
        tvBmiStatus = findViewById(R.id.tv_bmi_status);
        btnCalculate = findViewById(R.id.btn_calculate);
        warnText = findViewById(R.id.warningText);
        btnHistory = findViewById(R.id.HistoryButton);

        btnCalculate.setOnClickListener(view -> calculateBMI());
        btnHistory.setOnClickListener(view -> {
            Intent HB = new Intent(MainActivity.this, ListActivity.class);
            startActivity(HB);
        });
    }

    private void calculateBMI() {
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();

        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr) / 100; // แปลงจากเซนติเมตรเป็นเมตร
            double bmi = weight / (height * height);

            DecimalFormat df = new DecimalFormat("#.##");
            tvBmi.setText(df.format(bmi));

            String status;
            if (bmi < 18.5) {
                status = getString(R.string.underweight); // ดึงค่าจาก strings.xml
                tvBmiStatus.setBackgroundColor(getColor(R.color.orange));
            } else if (bmi < 24.9) {
                status = getString(R.string.normal); // ดึงค่าจาก strings.xml
                tvBmiStatus.setBackgroundColor(getColor(R.color.green));
            } else if (bmi < 29.9) {
                status = getString(R.string.overweight); // ดึงค่าจาก strings.xml
                tvBmiStatus.setBackgroundColor(getColor(R.color.orange));
            } else {
                status = getString(R.string.obese); // ดึงค่าจาก strings.xml
                tvBmiStatus.setBackgroundColor(getColor(R.color.red));
            }

            tvBmiStatus.setText(status);
            warnText.setText(getString(R.string.blank));

            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            FileOutputStream outputStream;
            FileInputStream inputStream;
            String FN = "history.json";
            try {
                JSONObject data = new JSONObject();
                data.put("date", date);
                data.put("weight", weight);
                data.put("bmiValue", df.format(bmi));
                data.put("bmiStatus", status);

                JSONArray dataArray;
                File file = new File(getExternalFilesDir(null), FN);
                if(file.exists()){
                    inputStream = new FileInputStream(file);
                    byte[] buffer = new byte[(int) file.length()];
                    inputStream.read(buffer);
                    inputStream.close();
                    String jsonString = new String(buffer);
                    dataArray = new JSONArray(jsonString);
                } else {
                    dataArray = new JSONArray();
                }
                dataArray.put(data);

                outputStream = new FileOutputStream(file);
                outputStream.write(dataArray.toString().getBytes());
                outputStream.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            warnText.setText(getString(R.string.warn));
        }
    }
}

class DecimalDigitsInputFilter implements InputFilter {
    private Pattern mPattern;
    DecimalDigitsInputFilter(int digits, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digits - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) +
                "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }
}