package com.example.budgettracker;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
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
    private TableLayout transTable;
    private LayoutInflater layoutInflater;
    private int idCounter = 0;
    public DataBaseHelper myDB;
    //private static String filePath = "transaction.db";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edDate = findViewById(R.id.txtDate);
        edCost = findViewById(R.id.txtAmount);
        edActivity = findViewById(R.id.txtActivity);
        tBalance = findViewById(R.id.txtBalance);
        btnPlus = findViewById(R.id.txtPlus);
        btnMinus = findViewById(R.id.txtMinus);
        transTable = findViewById(R.id.table_main);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        myDB = new DataBaseHelper(this);

        loadTable();

        setDateTimeField();
        edDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatePickerDialog.show();
                return false;
            }
        });


        btnMinus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick (View view) {
                View myView = layoutInflater.inflate(R.layout.table_row, null, false);

                TextView textDate = myView.findViewById(R.id.txtTableDate);
                TextView textAmount = myView.findViewById(R.id.txtTableAmount);
                TextView textCategory = myView.findViewById(R.id.txtTableCategory);

                double cost = Double.parseDouble(edCost.getText().toString());
                balance -= cost;
                tBalance.setText(df2.format(balance));

                textDate.setText(edDate.getText().toString());
                textAmount.setText("-" + df2.format(cost));
                textCategory.setText(edActivity.getText().toString());
                transTable.addView(myView);



                TransactionModel transaction = new TransactionModel();

                transaction.mId = idCounter;
                transaction.mDate = textDate.getText().toString();
                transaction.mAmount = -cost;
                transaction.mCategory = textCategory.getText().toString();

                String sql = "INSERT INTO \"Transaction\" VALUES " + transaction.toSQL();
                Log.i("SQL", sql);

                myDB.insertData(transaction.mDate, transaction.mAmount, transaction.mCategory);
            }

        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View myView = layoutInflater.inflate(R.layout.table_row, null, false);

                TextView textDate = myView.findViewById(R.id.txtTableDate);
                TextView textAmount = myView.findViewById(R.id.txtTableAmount);
                TextView textCategory = myView.findViewById(R.id.txtTableCategory);

                double cost = Double.parseDouble(edCost.getText().toString());
                balance += cost;
                tBalance.setText(df2.format(balance));

                textDate.setText(edDate.getText().toString());
                textAmount.setText(df2.format(balance));
                textCategory.setText(edActivity.getText().toString());
                transTable.addView(myView);



                TransactionModel transaction = new TransactionModel();

                transaction.mId = idCounter;
                transaction.mDate = edDate.getText().toString();
                transaction.mAmount = cost;
                transaction.mCategory = edActivity.getText().toString();

                String sql = "INSERT INTO \"Transaction\" VALUES" + transaction.toSQL();
                Log.i("SQL", sql);

                myDB.insertData(transaction.mDate, transaction.mAmount, transaction.mCategory);
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

    public void loadTable() {
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {
            Log.i("Database Empty", "Database has no records");
            return;
        }

        while (res.moveToNext()) {
            View myView = layoutInflater.inflate(R.layout.table_row, null, false);

            TextView textDate = myView.findViewById(R.id.txtTableDate);
            TextView textAmount = myView.findViewById(R.id.txtTableAmount);
            TextView textCategory = myView.findViewById(R.id.txtTableCategory);

            double cost = res.getDouble(2);
            balance += cost;
            String costS = df2.format(cost);

            textDate.setText(res.getString(1));
            textAmount.setText(costS);
            textCategory.setText(res.getString(3));
            transTable.addView(myView);

        }

        tBalance.setText(df2.format(balance));

    }






}
