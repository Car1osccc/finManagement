package com.financial.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


//Login page
public class MainActivity extends AppCompatActivity {
    EditText edt_id,edt_pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt_id = findViewById(R.id.edt_uid);
        edt_pwd =findViewById(R.id.edt_upwd);
        Button btn_login = findViewById(R.id.btn_login);
        //Button for login
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String userId=edt_id.getText().toString();
                    String userPwd=edt_pwd.getText().toString();
                    DBHelper dbuserHelper=new DBHelper(getApplicationContext());
                    User user = dbuserHelper.userlogin(userId,userPwd);

                    //If login successful, jump to the corresponding type interface
                    if(user!=null) {
                        Toast.makeText(getApplicationContext(), user.getUserId() + " Logged In Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        ArrayList<User> list = new ArrayList<>();
                        list.add(user);

                        intent = new Intent(getApplicationContext(), UserCenterActivity.class);
                        intent.putParcelableArrayListExtra("LoginUser", list);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"Login Failed! Wrong Password or Non-existing Account!",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Database Exception",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Button for registration
        Button btn_register=findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);

                startActivity(intent);
            }
        });
    }
}
