package com.financial.management;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

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
                List<String> dates = new ArrayList<>();
                while (cursor.moveToNext()) {
                    dates.add(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                    entries.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex("SUM")), cursor.getString(cursor.getColumnIndex(COLUMN_DATE))));
                    i++;
                }
                for (int j = 0; j < dates.size(); j++) {
                    System.out.println(dates.get(j));
                }
                System.out.println(entries.size());
                BarDataSet dataSet = new BarDataSet(entries, "Money"); // add entries to dataset
                BarData barData = new BarData(dataSet);
                barChart1.setData(barData);
                barChart1.setDrawGridBackground(false);
                barChart1.getDescription().setText("Daily income statistics");
                barChart1.getDescription().setPosition(900,100);                barChart1.getDescription().setTextSize(12);
                XAxis xAxis = barChart1.getXAxis();
                xAxis.setGranularity(1);
                xAxis.setLabelCount(dates.size());
                xAxis.setDrawGridLines(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return dates.get((int) value);
                    }
                });
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
                List<String> dates = new ArrayList<>();
                while (cursor.moveToNext()) {
                    dates.add(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                    entries.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex("SUM"))));
                    i++;
                }

                BarDataSet dataSet = new BarDataSet(entries, "Money"); // add entries to dataset
                BarData barData = new BarData(dataSet);
                barChart2.setData(barData);
                barChart2.getDescription().setText("Daily expenditure statistics");
                barChart2.getDescription().setPosition(900,100);
                barChart2.getDescription().setTextSize(12);
                XAxis xAxis = barChart2.getXAxis();
                xAxis.setGranularity(1);
                xAxis.setLabelCount(dates.size());
                xAxis.setDrawGridLines(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return dates.get((int) value);
                    }
                });
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
