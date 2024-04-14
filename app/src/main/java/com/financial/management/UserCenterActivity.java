package com.financial.management;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class UserCenterActivity extends AppCompatActivity {
    ArrayList<User> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        Intent intent=getIntent();
        list =intent.getParcelableArrayListExtra("LoginUser");
        User user=list.get(0);
        final String username=user.getUserId();
        TextView tv_welcome=findViewById(R.id.tv_welcome);
        tv_welcome.setText("Welcome, "+username);

        //Income and Expenses Managment
        ImageView btn_recordmanage =findViewById(R.id.btn_recordmanage);
        btn_recordmanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getApplicationContext(), ManageActivity.class);
                startActivity(intent1);
            }
        });
        //Income and Expenses Query
        ImageView btn_searchrecord=findViewById(R.id.btn_searchrecord);
        btn_searchrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(getApplicationContext(),SearchRecordActivity.class);
                startActivity(intent2);
            }
        });
        //Income and Expenses Statistics
        ImageView btn_calcmoney=findViewById(R.id.btn_calcmoney);
        btn_calcmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3=new Intent(getApplicationContext(),RecordChartActivity.class);
                startActivity(intent3);
            }
        });
        //Logout button
        ImageView btn_exit=findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(UserCenterActivity.this).setTitle("Logout action")
                        .setMessage("Are you sure you want to quit？")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
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
