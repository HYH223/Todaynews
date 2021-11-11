package com.example.everytown;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageButton room_reg;
    private ImageButton period;
    private ImageButton mypage;
    private ImageButton reload;

    private long backKeyPressedTime = 0;
    private Toast toast;

    SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
    SimpleDateFormat format2 = new SimpleDateFormat("MM");
    SimpleDateFormat format3 = new SimpleDateFormat("dd");

    long now = System.currentTimeMillis();
    Date date = new Date(now);

    int y1 = Integer.parseInt(format1.format(date));
    int m1 = Integer.parseInt(format2.format(date));
    int d1 = Integer.parseInt(format3.format(date));

    int y2 = Integer.parseInt(format1.format(date));
    int m2 = Integer.parseInt(format2.format(date));
    int d2 = Integer.parseInt(format3.format(date));

    String first = "20301231";
    String second = "20100101";

    phpDown task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        room_reg = (ImageButton) findViewById(R.id.room_reg);
        period = (ImageButton) findViewById(R.id.period);
        mypage = (ImageButton) findViewById(R.id.mypage);
        reload = (ImageButton) findViewById(R.id.reload);

        task = new phpDown();


        room_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent law = new Intent(MainActivity.this, law.class);
                law.putExtra("law", 1);
                law.putExtra("userID", getIntent().getStringExtra("userID"));
                startActivity(law);
            }
        });

        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mypage_act = new Intent(MainActivity.this, mypage.class);
                mypage_act.putExtra("userID", getIntent().getStringExtra("userID"));
                startActivity(mypage_act);
            }
        });

        period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate2();
                showDate1();
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                try {
                    task.execute("http://a98k98k.dothome.co.kr/marker.php?periodFirst=" + first + "&periodSecond=" + second);
                    Toast.makeText(getApplicationContext(), first + " ~ " + second, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    task.cancel(true);
                    task = new phpDown();
                    task.execute("http://a98k98k.dothome.co.kr/marker.php?periodFirst=" + first + "&periodSecond=" + second);
                    Toast.makeText(getApplicationContext(), first + " ~ " + second, Toast.LENGTH_LONG).show();
                }
            }
        });

        task.execute("http://a98k98k.dothome.co.kr/marker.php?periodFirst=" + first + "&periodSecond=" + second);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Double x = null,y=null;
        try {
            StringBuffer data = new StringBuffer();
            FileInputStream fis = openFileInput("myfile.txt");
            BufferedReader buffer = new BufferedReader
                    (new InputStreamReader(fis));
            String str = buffer.readLine();
            x = Double.parseDouble(str);
            str = buffer.readLine();
            y = Double.parseDouble(str);
            if(x==null||y==null){
                LatLng startingPoint = new LatLng(36.7989522, 127.072742);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(startingPoint));
            }else{
                LatLng startingPoint = new LatLng(x, y);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(startingPoint));
            }
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
            LatLng startingPoint = new LatLng(36.7989522, 127.072742);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 14));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(startingPoint));
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                Intent info = new Intent(MainActivity.this, info.class);
                info.putExtra("marker", Integer.parseInt(marker.getTitle()));
                info.putExtra("userID", getIntent().getStringExtra("userID"));
                startActivity(info);
                return false;
            }
        });
    }


    void showDate1() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y1 = year;
                m1 = month + 1;
                d1 = dayOfMonth;
                if (d1 > 0 && d1 < 10)
                    first = Integer.toString(y1) + Integer.toString(m1) + "0" + Integer.toString(d1);
                else
                    first = Integer.toString(y1) + Integer.toString(m1) + Integer.toString(d1);
            }
        }, Integer.parseInt(format1.format(date)), Integer.parseInt(format2.format(date)) - 1, Integer.parseInt(format3.format(date)));

        datePickerDialog.setMessage("입실 날짜");
        datePickerDialog.show();
    }


    void showDate2() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y2 = year;
                m2 = month + 1;
                d2 = dayOfMonth;
                if (d2 > 0 && d2 < 10) {
                    second = Integer.toString(y2) + Integer.toString(m2) + "0" + Integer.toString(d2);
                } else
                    second = Integer.toString(y2) + Integer.toString(m2) + Integer.toString(d2);
            }
        }, Integer.parseInt(format1.format(date)), Integer.parseInt(format2.format(date)) - 1, Integer.parseInt(format3.format(date)));

        datePickerDialog.setMessage("퇴실 날짜");
        datePickerDialog.show();
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
                    try {
                        item.setlatitude(infoObject.getString("latitude"));
                        item.setlongitude(infoObject.getString("longitude"));
                        item.setno(infoObject.getString("no"));
                        showMarker(Double.parseDouble(item.getlatitude()), Double.parseDouble(item.getlongitude()), item.getno());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }

    public void showMarker(Double x, Double y, String no) {
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(new LatLng(x, y)).title(no);
        mMap.addMarker(makerOptions);
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