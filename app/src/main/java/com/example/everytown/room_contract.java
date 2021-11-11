package com.example.everytown;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class room_contract extends AppCompatActivity {

    private ImageButton back;
    private Button next;
    private CheckBox agree;

    private long backKeyPressedTime = 0;
    private Toast toast;

    phpDown task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_contract);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        agree = findViewById(R.id.agree);
        int no = getIntent().getIntExtra("marker", 0);

        task = new phpDown();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(room_contract.this, law.class);
                intent.putExtra("law", 1);
                intent.putExtra("marker",no);
                intent.putExtra("userID",getIntent().getStringExtra("userID"));
                startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agree.isChecked()) {

                    Intent intent = new Intent(room_contract.this, MainActivity.class);
                    Toast.makeText(getApplicationContext(), "계약에 성공하셨습니다!", Toast.LENGTH_SHORT).show();
                    try {
                        task.execute("http://a98k98k.dothome.co.kr/contract.php?no=" + no);
                    } catch (Exception e) {
                        e.printStackTrace();
                        task.cancel(true);
                        task = new phpDown();
                        task.execute("http://a98k98k.dothome.co.kr/contract.php?no=" + no);
                    }
                    intent.putExtra("userID",getIntent().getStringExtra("userID"));
                    startActivity(intent);
                }
            }
        });
    }

    private class phpDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            String line = br.readLine();
                            if (line == null) break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }
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