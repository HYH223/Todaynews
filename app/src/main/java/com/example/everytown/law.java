package com.example.everytown;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

public class law extends AppCompatActivity {

    private ImageButton back;
    private Button next;
    private CheckBox agree;

    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_law);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        agree = findViewById(R.id.agree);

        String userID = getIntent().getStringExtra("userID");
        int no =getIntent().getIntExtra("marker",0);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int select = getIntent().getIntExtra("law",0);
                if (select == 1) {
                    Intent intent = new Intent(law.this, MainActivity.class);
                    intent.putExtra("userID", getIntent().getStringExtra("userID"));
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(law.this, info.class);
                    intent.putExtra("userID",getIntent().getStringExtra("userID"));
                    intent.putExtra("marker",no);
                    startActivity(intent);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agree.isChecked()) {
                    int select = getIntent().getIntExtra("law",0);
                    if (select == 1) {
                        Intent room_reg = new Intent(law.this, room_reg1.class);
                        room_reg.putExtra("userID",userID);
                        room_reg.putExtra("law", 1);
                        startActivity(room_reg);
                    }
                    else{
                        Intent room_contract = new Intent(law.this, room_contract.class);
                        room_contract.putExtra("law", 2);
                        room_contract.putExtra("marker",no);
                        room_contract.putExtra("userID",getIntent().getStringExtra("userID"));
                        startActivity(room_contract);
                    }
                }
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