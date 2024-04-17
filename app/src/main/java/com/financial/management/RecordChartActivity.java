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

import androidx.annotation.Nullable;
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
    private List<BarEntry> incomeEntries = new ArrayList<>();
    private List<BarEntry> expenseEntries = new ArrayList<>();
    private List<PieEntry> pieEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Spinner yearSpinner = findViewById(R.id.yearSpinner);
        BarChart incomeBarChart = findViewById(R.id.barChart1);
        BarChart expenseBarChart = findViewById(R.id.barChart2);
        PieChart pieChart = findViewById(R.id.pieChart);
        Cursor cursor = null;
        try {
            //If first create, set to MODE_PRIVATE
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

            setupYearSpinner(yearSpinner,pieChart,incomeBarChart,expenseBarChart);

            createPieChart(pieEntries, pieChart, null);
            createBarchart("Income", null, incomeBarChart, incomeEntries, "Total Income statistic");
            createBarchart("Expense", null, expenseBarChart, expenseEntries, "Total Expense statistic");


        } catch (SQLException e) {
            Toast.makeText(this, "Database exception", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } 
    }
    private void setupYearSpinner(Spinner yearSpinner, PieChart pieChart, BarChart incomeBarChart, BarChart expenseBarChart){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getYear(yearSpinner));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getItemAtPosition(position);
                createPieChart(pieEntries,pieChart,selectedYear);
                createBarchart("Income", selectedYear, incomeBarChart, incomeEntries, "Total Income statistic");
                createBarchart("Expense", selectedYear, expenseBarChart, expenseEntries, "Total Expense statistic");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    
    private void createPieChart(List<PieEntry> pieEntries, PieChart pieChart, @Nullable String year) {
        pieChart.clear();
        pieEntries.clear();
        // Calculate total income and add it to the chart
        float totalIncome = getTotalSum("Income",year);
        pieEntries.add(new PieEntry(totalIncome, "Income"));

        // Calculate total expenses and add it to the chart
        float totalExpense = getTotalSum("Expense",year);
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
    
    @SuppressLint("Range")
    private void createBarchart(String type, @Nullable String selectedYear, BarChart barChart, List<BarEntry> entries, String title){
        entries.clear();
        barChart.clear();
        boolean isYearSelected = selectedYear!=null & !Objects.equals(selectedYear, "All");

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT date, SUM(money) AS SUM FROM ").append(TABLE_NAME).append(" WHERE type = ?");
        ArrayList<String> args = new ArrayList<>();
        args.add(type);
        System.out.println(selectedYear);

        if (isYearSelected) {
            sqlBuilder.append(" AND date LIKE ?");
            args.add(selectedYear + "%");
        }
        sqlBuilder.append(" GROUP BY date");

        int i = 0;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sqlBuilder.toString(), args.toArray(new String[0]));
            if (cursor.getCount() == 0) {
                // listView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "No available record", Toast.LENGTH_SHORT).show();
            } else {
                List<String> dates = new ArrayList<>();
                while (cursor.moveToNext()) {
                    dates.add(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                    entries.add(new BarEntry(i, cursor.getFloat(cursor.getColumnIndex("SUM")), cursor.getString(cursor.getColumnIndex(COLUMN_DATE))));
                    i++;
                }

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
//                xAxis.setValueFormatter(new ValueFormatter() {
//                    @Override
//                    public String getFormattedValue(float value) {
//                        return dates.get((int) value);
//                    }
//                });
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < dates.size()) {
                            String dateString = dates.get(index);
                            String year = dateString.substring(0, 4);
                            String month = dateString.substring(4);
                            String formattedDate = "";
                            if (isYearSelected) {
                                formattedDate = getMonthName(Integer.parseInt(month));
                            } else{
                                formattedDate = getMonthName(Integer.parseInt(month))+" "+year;
                            }
                            return formattedDate;
                        } else {
                            return ""; // Return an empty string or handle the out-of-bounds case appropriately
                        }
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
            }


        } catch (SQLException e) {
            Toast.makeText(this, "Database exception", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        barChart.invalidate(); // refresh
    }
    
    @SuppressLint("Range")
    private float getTotalSum(String type, @Nullable String year) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT SUM(money) AS total FROM ").append(TABLE_NAME).append(" WHERE type = ?");
        ArrayList<String> args = new ArrayList<>();
        args.add(type);

        if (!Objects.equals(year, "All")) {
            sqlBuilder.append(" AND date LIKE ?");
            args.add(year + "%");
        }

        Cursor cursor = null;
        float total = 0;
        try {
            cursor = sqLiteDatabase.rawQuery(sqlBuilder.toString(), args.toArray(new String[0]));
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
    private List<String> getYear(Spinner yearSpinner) {
        String sql = "select " +COLUMN_DATE+" from "+ TABLE_NAME;
        Cursor c = sqLiteDatabase.rawQuery(sql, null);
        HashSet<String> yearSet = new HashSet<>();

        while (c.moveToNext()) {
            String date = c.getString(c.getColumnIndexOrThrow(COLUMN_DATE));
            String year = date.substring(0, 4); // Extract the first 4 characters as the year
            yearSet.add(year);
        }

        c.close();
        ArrayList<String> yearList = new ArrayList<>();
        yearList.add("All");  // Default item
        yearList.addAll(yearSet);
        return yearList;
    }

    // Helper method to get the month name from the month number
    private String getMonthName(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month - 1];
    }

}
