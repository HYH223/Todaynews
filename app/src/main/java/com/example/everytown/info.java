package com.example.everytown;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class info extends AppCompatActivity {

    private final String host = "ip";
    private final String username = "id";
    private final String password = "pw";
    private final int port = 21;
    private final String TAG = "Connect FTP";
    private ConnectFTP ConnectFTP;

    private ImageButton back;
    private Button next;
    private ImageView iv_1;
    private TextView tv_1;
    private TextView tv_2;
    private TextView tv_3;
    private TextView tv_4;
    private TextView tv_5;
    private TextView tv_6;
    private Bitmap bmImg;

    private long backKeyPressedTime = 0;
    private Toast toast;

    phpDown task;

    String photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        iv_1 = findViewById(R.id.iv_1);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);
        tv_4 = findViewById(R.id.tv_4);
        tv_5 = findViewById(R.id.tv_5);
        tv_6 = findViewById(R.id.tv_6);

        task = new phpDown();

        int no = getIntent().getIntExtra("marker", 0);

        try {
            task.execute("http://a98k98k.dothome.co.kr/info_load.php?no=" + no);
        } catch (Exception e) {
            e.printStackTrace();
            task.cancel(true);
            task = new phpDown();
            task.execute("http://a98k98k.dothome.co.kr/info_load.php?no=" + no);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ConnectFTP = new ConnectFTP();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean status = false;
                        try {
                            status = ConnectFTP.ftpConnect(host, username, password, port);
                            if (status == true) {
                                Log.d(TAG, "Connection Success");
                            } else {
                                Log.d(TAG, "Connection failed");
                            }

                            InputStream image = ConnectFTP.retrieveFileStream(photo);
                            bmImg = BitmapFactory.decodeStream(image);
                            iv_1.setImageBitmap(bmImg);

                            boolean result = ConnectFTP.ftpDisconnect();
                            if (result == true)
                                Log.d(TAG, "DisConnection Success");
                            else
                                Log.d(TAG, "DisConnection Success");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        },1000);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int select = getIntent().getIntExtra("user_reg",0);
                if (select == 1) {
                    Intent intent = new Intent(info.this, user_reg.class);
                    intent.putExtra("userID", getIntent().getStringExtra("userID"));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(info.this, MainActivity.class);
                    intent.putExtra("userID", getIntent().getStringExtra("userID"));
                    startActivity(intent);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent law = new Intent(info.this, law.class);
                law.putExtra("law", 2);
                law.putExtra("marker", no);
                law.putExtra("userID", getIntent().getStringExtra("userID"));
                startActivity(law);
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

        protected void onPostExecute(String str) {
            infoItem item = new infoItem();
            try {
                JSONObject jsonObject = new JSONObject(str);

                JSONArray infoArray = jsonObject.getJSONArray("INFO");

                for (int i = 0; i < infoArray.length(); i++) {
                    JSONObject infoObject = infoArray.getJSONObject(i);

                    item.setno(infoObject.getString("no"));
                    item.setuserID(infoObject.getString("userID"));
                    item.setaddress(infoObject.getString("address"));
                    item.setarea(infoObject.getString("area"));
                    item.setOption(infoObject.getString("Option"));
                    item.setperiodFirst(infoObject.getString("periodFirst"));
                    item.setperiodSecond(infoObject.getString("periodSecond"));
                    item.setprice(infoObject.getString("price"));
                    item.setmemo(infoObject.getString("memo"));
                    item.setlatitude(infoObject.getString("latitude"));
                    item.setlongitude(infoObject.getString("longitude"));
                    item.setphoto(infoObject.getString("photo"));

                }

            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }

            tv_1.setText(item.getaddress());
            tv_2.setText(item.getarea());
            tv_3.setText(item.getOption());
            tv_4.setText(item.getperiodFirst() + " ~ " + item.getperiodSecond());
            tv_5.setText(item.getprice());
            tv_6.setText(item.getmemo());
            photo = item.getphoto();

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