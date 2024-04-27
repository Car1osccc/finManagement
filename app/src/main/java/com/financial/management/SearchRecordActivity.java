package com.financial.management;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Income and Expense Record Search
public class SearchRecordActivity extends AppCompatActivity {
    private String[] type_data = {"", "Income", "Expense"};
    Spinner spin_date, spin_type;
    ListView listView;
    TextView tv_show;
    float sum=0;
    //数据库
    private String selectDate, selectType;
    private static final String DATABASE_NAME = "Test.db";
    private static final String TABLE_NAME = "record";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_MONEY = "money";
    private static final String COLUMN_STATE = "state";
    private SQLiteDatabase sqLiteDatabase = null;

    private void selectSumMoney() {
        String sql;
        //If no query time or query type, query the entire table
        if (TextUtils.isEmpty(selectDate) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME;
            //If has query time but no query type, query the specified content
        } else if (!TextUtils.isEmpty(selectDate) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME + " where date='" + selectDate + "'";
            //If no query time but has query type, query the specified content
        } else if (TextUtils.isEmpty(selectDate) && !TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME + " where type='" + selectType+"'";
        } else {
            sql ="select * from " + TABLE_NAME + " where date='" + selectDate + "' and type='" + selectType+"'";
        }
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        while (cursor.moveToNext()) {

            float money = cursor.getFloat(cursor.getColumnIndex(COLUMN_MONEY));
            String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
            if (type.equals("Income"))
            {
                sum=sum+money;
            }
            if (type.equals("Expense"))
            {
                sum=sum-money;
            }

            //list.add(map);
        }
        String money2=String.valueOf(sum);
        tv_show.setText(money2);
        sum=0;
    }

    private void selectData() {
        String sql;
        //If no query time or query type, select the entire table
        if (TextUtils.isEmpty(selectDate) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME;
            //If has query time but no query type, select the specified content
        } else if (!TextUtils.isEmpty(selectDate) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME + " where date='" + selectDate + "'";
            //If no query time but has query type, select the specified content
        } else if (TextUtils.isEmpty(selectDate) && !TextUtils.isEmpty(selectType)) {//如果没有查询时间，有查询类型
            sql = "select * from " + TABLE_NAME + " where type='" + selectType+"'";
        } else {
            sql = "select * from " + TABLE_NAME + " where date='" + selectDate + "' and type='" + selectType+"'";
        }

        //Encapsulate the queried data into Cursor
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (cursor.getCount() == 0) {
            //If no data is found, the list will not be displayed
            listView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "N/A", Toast.LENGTH_SHORT).show();
        } else {
            //If there is data, display the list
            listView.setVisibility(View.VISIBLE);
            while (cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                float money = cursor.getFloat(cursor.getColumnIndex(COLUMN_MONEY));
                String state = cursor.getString(cursor.getColumnIndex(COLUMN_STATE));
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", String.valueOf(id));
                map.put("date", date);
                map.put("type", type);
                map.put("money", String.valueOf(money));
                map.put("state", state);
                list.add(map);
            }
            //Create SimpleAdapter
            SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                    list,
                    R.layout.record_item_layout,
                    new String[]{"id", "date", "type", "money", "state"},
                    new int[]{R.id.list_id, R.id.list_date, R.id.list_type, R.id.list_money, R.id.list_state});
            listView.setAdapter(simpleAdapter);

        }
        cursor.close();
    }

    //Time and category spinner click event
    private void initClick() {
        spin_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectType = type_data[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt_date = findViewById(R.id.edt_date);
                selectDate = edt_date.getText().toString();
                selectData();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_record);
        tv_show=findViewById(R.id.tv_show);
        try {
            //Open database. If first time, create in MODE_PRIVATE mode
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            //Although it is called every time, the table is only created once

            //Execute query
            listView = findViewById(R.id.searchlistview);
            selectData();
        } catch (SQLException e) {
            Toast.makeText(this, "Database Exception", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter1 = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_data);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_type = findViewById(R.id.spin_type);
        spin_type.setAdapter(adapter1);
        initClick();
        Button btn_calc=findViewById(R.id.btn_calc);
        btn_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSumMoney();

            }
        });

    }
}
