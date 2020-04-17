package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
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

import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Search extends AppCompatActivity {

    private DatePickerDialog mFDatePickerDialog, mTDatePickerDialog;
    private static DecimalFormat df2 = new DecimalFormat("0.00");
    private EditText edDateFrom, edDateTill, edMinPrice, edMaxPrice;
    private Button btnSearch, btnExit;
    private CheckBox boxInc, boxExp, boxDate;
    private TableLayout transTable;
    public DataBaseHelper myDB;
    private boolean hasRows;
    private LayoutInflater layoutInflater;



    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edDateFrom = findViewById(R.id.txtBeforeDate);
        edDateTill = findViewById(R.id.txtAfterDate);
        edMinPrice = findViewById(R.id.txtMin);
        edMaxPrice = findViewById(R.id.txtMax);
        btnSearch = findViewById(R.id.btnSearchT);
        btnExit = findViewById(R.id.btnClose);
        transTable = findViewById(R.id.table_sub);
        boxInc = findViewById(R.id.boxInc);
        boxExp = findViewById(R.id.boxExp);
        boxDate = findViewById(R.id.boxDates);

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        myDB = new DataBaseHelper(this);

        setDateTimeField();
        edDateFrom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFDatePickerDialog.show();
                return false;
            }
        });

        edDateTill.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTDatePickerDialog.show();
                return false;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadTable();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO:
                // This function closes Activity Two
                // Hint: use Context's finish() method
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        mFDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                edDateFrom.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mFDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        mTDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                edDateTill.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mTDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    public void loadTable() {

        if (hasRows == true) {
            while(transTable.getChildCount() > 1) {
                transTable.removeView(transTable.getChildAt(transTable.getChildCount() - 1));
            };
            hasRows = false;
        }

        Cursor res;
        String startDate = edDateFrom.getText().toString();
        String endDate = edDateTill.getText().toString();
        String sMin = edMinPrice.getText().toString();
        String sMax = edMaxPrice.getText().toString();
        Double minPrice = 0.00000001;
        Double maxPrice = 99999999.99;
        Double negMin = minPrice * -1;
        Double negMax = maxPrice * -1;

        if (!sMin.equals("")){
            minPrice = Double.parseDouble(sMin);
            negMin = minPrice * -1;
        }
        if (!sMax.equals("")) {
            maxPrice = Double.parseDouble(sMax);
            negMax = maxPrice * -1;
        }

        if (boxDate.isChecked() && (!boxInc.isChecked() && boxExp.isChecked())) {
            res = myDB.getSelectedData(negMax, negMin); //Check only negative values
        }
        else if (boxDate.isChecked() && (boxInc.isChecked() && boxExp.isChecked())) {
            res = myDB.getSelectedData(minPrice, maxPrice, negMin, negMax); //Check positive and negative
        }
        else if (boxDate.isChecked() && (boxInc.isChecked() && (!boxExp.isChecked()))) {
            res = myDB.getSelectedData(minPrice, maxPrice); //Check only positive values
        }
        else if (!boxInc.isChecked() && boxExp.isChecked()) {
            res = myDB.getSelectedData(startDate, endDate, negMax, negMin);
        }
        else if (boxInc.isChecked() && boxExp.isChecked()) {
            res = myDB.getSelectedData(startDate, endDate, minPrice, maxPrice, negMin, negMax);
        }
        else {
            res = myDB.getSelectedData(startDate, endDate, minPrice, maxPrice);
        }

        if (res.getCount() == 0) {
            Log.i("Database Empty", "Database has no records");
            return;
        }

        while (res.moveToNext()) {

            hasRows = true;
            View myView = layoutInflater.inflate(R.layout.table_row, null, false);

            TextView textDate = myView.findViewById(R.id.txtTableDate);
            TextView textAmount = myView.findViewById(R.id.txtTableAmount);
            TextView textCategory = myView.findViewById(R.id.txtTableCategory);

            double cost = res.getDouble(2);
            String costS = df2.format(cost);

            textDate.setText(res.getString(1));
            textAmount.setText(costS);
            textCategory.setText(res.getString(3));
            transTable.addView(myView);

        }

    }
}
