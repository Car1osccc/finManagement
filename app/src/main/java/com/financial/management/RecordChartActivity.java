package com.financial.management;


import static java.security.AccessController.getContext;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
        PieChart pieChart = findViewById(R.id.pieChart);

        Cursor cursor = null;
        try {
            //If first create, set to MODE_PRIVATE
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            String sql1 = "select date, SUM(money) AS SUM from " + TABLE_NAME + " WHERE type =  'Income'"  + " GROUP BY date" ;
            String sql2 = "select date, SUM(money) AS SUM from " + TABLE_NAME + " WHERE type =  'Expense'"  + " GROUP BY date" ;

            List<BarEntry> entries = new ArrayList<>();
            List<PieEntry> pieEntries = new ArrayList<>();

            // Calculate total income and add it to the chart
            float totalIncome = calculateTotal("Income");
            pieEntries.add(new PieEntry(totalIncome, "Income"));

            // Calculate total expenses and add it to the chart
            float totalExpense = calculateTotal("Expense");
            pieEntries.add(new PieEntry(totalExpense, "Expense"));

            PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
            pieChart.setCenterText("Total statistics");
            pieDataSet.setColors(Color.MAGENTA, Color.BLUE);

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            // Customize the appearance of the chart
            pieChart.getDescription().setEnabled(false);
            pieChart.setDrawEntryLabels(false);
            pieChart.setUsePercentValues(true);

            // Configure the Legend
            Legend legend = pieChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setEnabled(true);

            // Set a custom ValueFormatter to display data values on the chart
            pieData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.0f%%", value);
                }
            });
            // Customize the appearance of the percentage text
            pieData.setValueTextSize(14f);
            pieData.setValueTextColor(Color.WHITE);

            pieChart.invalidate(); // Refresh the chart


            cursor = sqLiteDatabase.rawQuery(sql1, null);
            int i = 0;
            if (cursor.getCount() == 0) {
                //查无数据则怒不显示列表
                // listView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "No available record", Toast.LENGTH_SHORT).show();
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
                BarDataSet dataSet = new BarDataSet(entries, "hkd"); // add entries to dataset
                BarData barData = new BarData(dataSet);
                barChart1.setData(barData);
                barChart1.getDescription().setText("");

                // Set custom center-aligned title
                TextView chartTitle = new TextView(this);
                chartTitle.setText("Total income statistics");
                chartTitle.setTextSize(14);
                chartTitle.setTextColor(Color.BLUE);
                barChart1.addView(chartTitle);

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

                YAxis yAxis = barChart1.getAxisLeft();
                yAxis.setGranularity(1);
                yAxis.setDrawGridLines(false);
                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                yAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int intValue = (int) value;
                        return String.valueOf(intValue);
                    }
                });

                YAxis y2Axis = barChart1.getAxisRight();
                y2Axis.setGranularity(1);
                y2Axis.setDrawGridLines(false);
                y2Axis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                y2Axis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int intValue = (int) value;
                        return String.valueOf(intValue);
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
                Toast.makeText(getApplicationContext(), "No available record", Toast.LENGTH_SHORT).show();
            }else{
                List<String> dates = new ArrayList<>();
                while (cursor.moveToNext()) {
                    dates.add(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                    entries.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex("SUM"))));
                    i++;
                }

                BarDataSet dataSet = new BarDataSet(entries, "hkd"); // add entries to dataset
                BarData barData = new BarData(dataSet);
                barChart2.setData(barData);
                barChart2.getDescription().setText("");

                TextView chartTitle = new TextView(this);
                chartTitle.setText("Total expenditure statistics");
                chartTitle.setTextSize(14);
                chartTitle.setTextColor(Color.BLUE);
                barChart2.addView(chartTitle);

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

                YAxis yAxis = barChart2.getAxisLeft();
                yAxis.setGranularity(1);
                yAxis.setDrawGridLines(false);
                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                yAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int intValue = (int) value;
                        return String.valueOf(intValue);
                    }
                });

                YAxis y2Axis = barChart2.getAxisRight();
                y2Axis.setGranularity(1);
                y2Axis.setDrawGridLines(false);
                y2Axis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                y2Axis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int intValue = (int) value;
                        return String.valueOf(intValue);
                    }
                });

                barChart2.invalidate(); // refresh


            }



        } catch (SQLException e) {
            Toast.makeText(this, "Database exception", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private float calculateTotal(String type) {
        String sql = "SELECT SUM(money) AS total FROM " + TABLE_NAME + " WHERE type = '" + type + "'";
        Cursor cursor = null;
        float total = 0;

        try {
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            cursor = sqLiteDatabase.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                total = cursor.getFloat(cursor.getColumnIndex("total"));
            }
        } catch (SQLException e) {
            Toast.makeText(this, "Database exception", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return total;
    }
}
