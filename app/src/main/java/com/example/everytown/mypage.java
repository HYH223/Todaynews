package com.example.everytown;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

public class mypage extends AppCompatActivity {
    private ImageButton back;
    private Button logout;
    private Button btn_start;
    private Button btn_regr;
    private TextView id_copy;

    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        id_copy = findViewById(R.id.id_copy);
        String userID = getIntent().getStringExtra("userID");
        id_copy.setText(userID+"님 반갑습니다!");

        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mypage.this, point.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });

        btn_regr = findViewById(R.id.btn_regr);
        btn_regr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mypage.this,user_reg.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mypage.this, MainActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });

       logout = findViewById(R.id.logout);
       logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mypage.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finishAffinity();
            System.runFinalization();
            System.exit(0);
        }
    }
}