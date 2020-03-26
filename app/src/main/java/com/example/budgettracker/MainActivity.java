package com.example.budgettracker;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;


@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    private DatePickerDialog mDatePickerDialog;
    private static DecimalFormat df2 = new DecimalFormat("0.00");
    private TextView tBalance, tHistory;
    private EditText edDate, edCost, edActivity;
    private Button btnPlus, btnMinus;
    private double balance;
    private String textBal, textHis;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT_HISTORY = "textHistory";
    public static final String TEXT_BALANCE = "textBalance";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edDate = findViewById(R.id.txtDate);
        edCost = findViewById(R.id.txtAmount);
        edActivity = findViewById(R.id.txtActivity);
        tBalance = findViewById(R.id.txtBalance);
        tHistory = findViewById(R.id.txtHistory);
        btnPlus = findViewById(R.id.txtPlus);
        btnMinus = findViewById(R.id.txtMinus);

        loadData();
        updateViews();

        setDateTimeField();
        edDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatePickerDialog.show();
                return false;
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double cost = Double.parseDouble(edCost.getText().toString());
                String activity = "Spent $" + df2.format(cost) + " on " + edDate.getText().toString() + " for " + edActivity.getText().toString();
                System.out.println(activity);
                balance -= cost;
                tBalance.setText(df2.format(balance));
                tHistory.append(activity + "\n");
                saveData();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double cost = Double.parseDouble(edCost.getText().toString());
                String activity = "Added $" + df2.format(cost) + " on " + edDate.getText().toString() + " from " + edActivity.getText().toString();
                balance += cost;
                tBalance.setText(df2.format(balance));
                tHistory.append(activity + "\n");
                saveData();
            }
        });
    }

    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                edDate.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT_HISTORY, tHistory.getText().toString());
        editor.putString(TEXT_BALANCE, tBalance.getText().toString());

        editor.apply();
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT);
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        textBal = sharedPreferences.getString(TEXT_BALANCE, "0.00");
        textHis = sharedPreferences.getString(TEXT_HISTORY, "");
        if (textHis.length() > 0) {
            textHis = textHis.trim() + "\n";
        }
        balance = Double.parseDouble(textBal);

    }

    public void updateViews() {
        tHistory.setText(textHis);
        tBalance.setText(textBal);
    }

    public void clear() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }



}
