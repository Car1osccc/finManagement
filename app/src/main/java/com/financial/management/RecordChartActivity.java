package com.financial.management;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
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
        BarChart barChart = findViewById(R.id.barChart);
        Cursor cursor = null;
        try {
            //打开数据库，如果是第一次会创建该数据库，模式为MODE_PRIVATE
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            String sql = "select date, SUM(money) AS SUM from " + TABLE_NAME + " GROUP BY date";

            List<BarEntry> entries = new ArrayList<>();
            cursor = sqLiteDatabase.rawQuery(sql, null);
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
                BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
                BarData barData = new BarData(dataSet);
                barChart.setData(barData);
                barChart.invalidate(); // refresh
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
