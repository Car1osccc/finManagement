package com.financial.management;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ManageActivity extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase = null;
    private int selectId = -1;
    EditText edt_date, edt_type, edt_money, edt_state;
    TextView tv_test;

    private static final String DATABASE_NAME = "Test.db";
    private static final String TABLE_NAME = "record";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_MONEY = "money";
    private static final String COLUMN_STATE = "state";

    //Create table
    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME
            + "(" + COLUMN_ID + " integer primary key autoincrement," + COLUMN_DATE + " text," + COLUMN_TYPE
            + " text," + COLUMN_MONEY + " float," + COLUMN_STATE + " text)";

    //Customized query method
    private void selectData() {
        String sql = "select * from " + TABLE_NAME ;
        //Encapsulate query data into Cursor
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        //Loop through the Cursor, then put it into the map respectively, store it in list for convinence
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
        final ListView listView = findViewById(R.id.recordlistview);
        //Binding adapter
        listView.setAdapter(simpleAdapter);
        //Set ListView click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView tempList = (ListView) parent;


                View mView = tempList.getChildAt(position);
                TextView list_id = mView.findViewById(R.id.list_id);
                TextView list_date = mView.findViewById(R.id.list_date);
                TextView list_type = mView.findViewById(R.id.list_type);
                TextView list_money = mView.findViewById(R.id.list_money);
                TextView list_state = mView.findViewById(R.id.list_state);

                String rid = list_id.getText().toString();
                String date = list_date.getText().toString();
                String type = list_type.getText().toString();
                String money = list_money.getText().toString();
                String state = list_state.getText().toString();

                tv_test.setText(rid);
                edt_date.setText(date);
                edt_type.setText(type);
                edt_money.setText(money);
                edt_state.setText(state);
                selectId = Integer.parseInt(rid);

            }
        });
    }

    private void showDatePicker() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Format the date as you need, ensuring month is two digits
                    String formattedMonth = String.format("%02d", monthOfYear + 1); // +1 because Calendar month is zero-based
                    String date = year1 + formattedMonth;
                    edt_date.setText(date); // Assuming you have edt_date as a TextView now
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        try {
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            sqLiteDatabase.execSQL(CREATE_TABLE);
            //Execute query
            selectData();
        } catch (SQLException e) {
            Toast.makeText(this, "Database Exception", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        tv_test = findViewById(R.id.tv_test);
        edt_date = findViewById(R.id.edt_date);
        edt_type = findViewById(R.id.edt_type);
        edt_type.setVisibility(View.GONE);
        edt_money = findViewById(R.id.edt_money);
        edt_state = findViewById(R.id.edt_state);

        Button btnPickDate = findViewById(R.id.btn_datePicker);
        Spinner spinnerType = findViewById(R.id.spinner_type);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected text
                String selectedItem = parent.getItemAtPosition(position).toString();
                // Update TextView
                edt_type.setText(selectedItem);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                edt_type.setText("");
            }
        });

        // Initialize spinner with type choices
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Date Picker Dialog
        btnPickDate.setOnClickListener(v -> showDatePicker());

        //Button for creation
        Button btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_date.getText().toString().equals("") | edt_type.getText().toString().equals("") | edt_money.getText().toString().equals("") | edt_state.getText().toString().equals("")) {
                    Toast.makeText(ManageActivity.this, "Data can't be NULL!", Toast.LENGTH_LONG).show();
                    return;
                }

                String date = edt_date.getText().toString();
                String type = edt_type.getText().toString();
                String money = edt_money.getText().toString();
                String state = edt_state.getText().toString();

                String sql = "insert into " + TABLE_NAME + "(" + COLUMN_DATE + "," + COLUMN_TYPE + "," + COLUMN_MONEY + "," + COLUMN_STATE + ") " +
                        "values('" + date + "','" + type + "','" + money + "','" + state + "')";
                sqLiteDatabase.execSQL(sql);
                Toast.makeText(getApplicationContext(), "Data Added Successfully!", Toast.LENGTH_LONG).show();

                //Refresh display list
                selectData();

                //Delete data
                tv_test.setText("");
                edt_date.setText("");
                edt_type.setText("");
                edt_money.setText("");
                edt_state.setText("");
            }
        });

        //Button for update
        Button btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If nothing selected
                if (selectId == -1) {
                    Toast.makeText(getApplicationContext(), "Select the Row to Edit!", Toast.LENGTH_LONG).show();
                    return;
                }
                //If selected data is empty
                if (edt_date.getText().toString().equals("") | edt_type.getText().toString().equals("") | edt_money.getText().toString().equals("") | edt_state.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Data Can't be NULL!", Toast.LENGTH_LONG).show();
                    return;
                }

                String date = edt_date.getText().toString();
                String type = edt_type.getText().toString();
                String money = edt_money.getText().toString();
                String state = edt_state.getText().toString();

                String sql = "update " + TABLE_NAME + " set " + COLUMN_DATE + "='" + date + "',type='" + type + "',money='" + money + "',state='" + state + "' where id=" + selectId;
                sqLiteDatabase.execSQL(sql);
                Toast.makeText(getApplicationContext(), "Data Edited Successfully!", Toast.LENGTH_LONG).show();
                //Refresh display list
                selectData();
                selectId = -1;
                //Delete data
                tv_test.setText("");
                edt_date.setText("");
                edt_type.setText("");
                edt_money.setText("");
                edt_state.setText("");
            }
        });

        //Button for deletion
        Button btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectId == -1) {
                    Toast.makeText(ManageActivity.this, "Select the Row to Delete!", Toast.LENGTH_LONG).show();
                    return;
                }

                //Define delete dialog
                AlertDialog dialog = new AlertDialog.Builder(ManageActivity.this).setTitle("Delete Alert")
                        .setMessage("Are You Sure to Delete?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String sql = "delete from " + TABLE_NAME + " where id=" + selectId;
                                sqLiteDatabase.execSQL(sql);

                                Toast.makeText(getApplicationContext(), "Data Deleted Successfully!", Toast.LENGTH_LONG).show();
                                selectData();
                                selectId = -1;

                                //Delete data
                                tv_test.setText("");
                                edt_date.setText("");
                                edt_type.setText("");
                                edt_money.setText("");
                                edt_state.setText("");
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();
            }
        });
    }
}
