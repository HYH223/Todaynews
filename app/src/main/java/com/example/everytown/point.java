package com.example.everytown;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class point extends AppCompatActivity {

    private ImageButton back;
    private EditText st_point;
    private Button next;

    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point);

        back = findViewById(R.id.back);
        st_point = findViewById(R.id.st_point);
        next = findViewById(R.id.next);
        String userID = getIntent().getStringExtra("userID");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(point.this, mypage.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = st_point.getText().toString();
                if (address.equals("")) {
                    Toast.makeText(getApplicationContext(), "빈칸을 채워주세요!", Toast.LENGTH_LONG).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Geocoder geocoder = new Geocoder(point.this, Locale.KOREA);
                                List<Address> addresses = geocoder.getFromLocationName(address, 3);

                                StringBuffer buffer = new StringBuffer();
                                for (Address t : addresses) {
                                    buffer.append(t.getLatitude() + ", " + t.getLongitude() + "\n");
                                }
                                String latitude = String.format("%.9f", addresses.get(0).getLatitude());
                                String longitude = String.format("%.9f", addresses.get(0).getLongitude());

                                try {
                                    FileOutputStream fos = openFileOutput
                                            ("myfile.txt",
                                                    Context.MODE_PRIVATE);
                                    PrintWriter out = new PrintWriter(fos);
                                    out.println(latitude + "\n" + longitude);
                                    out.close();

                                    Intent intent = new Intent(point.this, mypage.class);
                                    intent.putExtra("userID", userID);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
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
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}