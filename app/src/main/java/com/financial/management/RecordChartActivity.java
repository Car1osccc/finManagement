package com.financial.management;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class RecordChartActivity extends AppCompatActivity {
    private static final String DATABASE_NAME = "Test.db";

    private static final String TABLE_NAME = "record";

    private static final String COLUMN_DATE = "date";

    private SQLiteDatabase sqLiteDatabase = null;
    private List<BarEntry> entries = new ArrayList<>();
    private List<PieEntry> pieEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Spinner yearSpinner = findViewById(R.id.yearSpinner);
        BarChart barChart1 = findViewById(R.id.barChart1);
        BarChart barChart2 = findViewById(R.id.barChart2);
        PieChart pieChart = findViewById(R.id.pieChart);

        Cursor cursor = null;
        try {
            //If first create, set to MODE_PRIVATE
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            String sql1 = "select date, SUM(money) AS SUM from " + TABLE_NAME + " WHERE type =  'Income'"  + " GROUP BY date" ;
            String sql2 = "select date, SUM(money) AS SUM from " + TABLE_NAME + " WHERE type =  'Expense'"  + " GROUP BY date" ;

            addYearSelection(yearSpinner);

            createPieChart(pieEntries, pieChart);
            createBarchart(sql1, barChart1, entries, "Total Income statistic");
            entries = new ArrayList<>();
            createBarchart(sql2, barChart2, entries, "Total Expense statistic");

//            yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    String selectedYear = (String) parent.getItemAtPosition(position);
//                    if(!Objects.equals(selectedYear, "")){
//                        System.out.println("Selected Year: " + selectedYear);
//                        String sql = "SELECT " + COLUMN_DATE + ", SUM(money) AS SUM FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " LIKE '%"+selectedYear+"%' GROUP BY " + COLUMN_DATE;
//                        entries = new ArrayList<>();
//                        createBarchart(sql, barChart1, entries, "Total Income statistic");
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                }
//            });


        } catch (SQLException e) {
            Toast.makeText(this, "Database exception", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void updateChart(String selectedYear) {
    }

    private void createPieChart(List<PieEntry> pieEntries, PieChart pieChart) {
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
    }

    private void addYearSelection(Spinner yearSpinner) {
        String sql = "select " +COLUMN_DATE+" from "+ TABLE_NAME;
        Cursor c = sqLiteDatabase.rawQuery(sql, null);
        HashSet<String> yearSet = new HashSet<>();

        while (c.moveToNext()) {
            String date = c.getString(c.getColumnIndexOrThrow(COLUMN_DATE));
            String year = date.substring(0, 4); // Extract the first 4 characters as the year
            yearSet.add(year);
        }

        c.close();
        ArrayList<String> yearList = new ArrayList<>(yearSet);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
    }

    @SuppressLint("Range")
    private void createBarchart(String sql, BarChart barChart, List<BarEntry> entries, String title){
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        int i = 0;
        if (cursor.getCount() == 0) {
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
            barChart.setData(barData);
            barChart.getDescription().setText("");

            // Set custom center-aligned title
            TextView chartTitle = new TextView(this);
            chartTitle.setText(title);
            chartTitle.setTextSize(14);
            chartTitle.setTextColor(Color.BLUE);
            barChart.addView(chartTitle);

            XAxis xAxis = barChart.getXAxis();
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

            YAxis yAxis = barChart.getAxisLeft();
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

            YAxis y2Axis = barChart.getAxisRight();
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

            barChart.invalidate(); // refresh
        }
    }

    @SuppressLint("Range")
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
