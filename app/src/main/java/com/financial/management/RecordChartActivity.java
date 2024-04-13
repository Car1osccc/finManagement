package com.financial.management;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.util.ArrayList;
import java.util.List;

public class RecordChartActivity extends AppCompatActivity {
    private static final String DATABASE_NAME = "Test.db";

    private static final String TABLE_NAME = "record";

    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_MONEY = "money";

    private SQLiteDatabase sqLiteDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        BarChart barChart1 = findViewById(R.id.barChart1);
        BarChart barChart2 = findViewById(R.id.barChart2);
        Cursor cursor = null;
        try {
            //打开数据库，如果是第一次会创建该数据库，模式为MODE_PRIVATE
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            String sql1 = "select date, SUM(money) AS SUM from " + TABLE_NAME + " WHERE type =  '收入'"  + " GROUP BY date" ;
            String sql2 = "select date, SUM(money) AS SUM from " + TABLE_NAME + " WHERE type =  '支出'"  + " GROUP BY date" ;

            List<BarEntry> entries = new ArrayList<>();
            cursor = sqLiteDatabase.rawQuery(sql1, null);
            int i = 0;
            if (cursor.getCount() == 0) {
                //查无数据则怒不显示列表
                // listView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "无数据", Toast.LENGTH_SHORT).show();
            }else{
                while (cursor.moveToNext()) {
                    entries.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex("SUM")), cursor.getString(cursor.getColumnIndex(COLUMN_DATE))));
                    i++;
                }
                System.out.println(entries.size());
                BarDataSet dataSet = new BarDataSet(entries, "Money"); // add entries to dataset
                BarData barData = new BarData(dataSet);
                barChart1.setData(barData);
                barChart1.setDrawGridBackground(false);
                barChart1.getDescription().setText("Daily income statistics");
                barChart1.getDescription().setPosition(900,100);                barChart1.getDescription().setTextSize(12);
                barChart1.getXAxis().setDrawGridLines(false);
                barChart1.invalidate(); // refresh
            }

            entries = new ArrayList<>();
            cursor = sqLiteDatabase.rawQuery(sql2, null);
            i = 0;
            if (cursor.getCount() == 0) {
                //查无数据则怒不显示列表
                // listView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "无数据", Toast.LENGTH_SHORT).show();
            }else{
                while (cursor.moveToNext()) {
                    entries.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex("SUM")), cursor.getString(cursor.getColumnIndex(COLUMN_DATE))));
                    i++;
                }
                System.out.println(entries.size());
                BarDataSet dataSet = new BarDataSet(entries, "Money"); // add entries to dataset
                BarData barData = new BarData(dataSet);
                barChart2.setData(barData);
                barChart2.getDescription().setText("Daily expenditure statistics");
                barChart2.getDescription().setPosition(900,100);
                barChart2.getDescription().setTextSize(12);
                barChart2.getXAxis().setDrawGridLines(false);
                barChart2.invalidate(); // refresh
            }



        } catch (SQLException e) {
            Toast.makeText(this, "数据库异常!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }
}
