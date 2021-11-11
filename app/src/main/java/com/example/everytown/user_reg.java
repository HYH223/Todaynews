package com.example.everytown;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class user_reg extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ImageButton back;
    private ListView list;
    private Button next;

    private long backKeyPressedTime = 0;
    private Toast toast;

    phpDown task;

    final ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> list_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reg);

        String userID = getIntent().getStringExtra("userID");

        list = findViewById(R.id.list);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        task = new phpDown();

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"item 1", "item 2"},
                new int[]{android.R.id.text1, android.R.id.text2});
        list.setAdapter(simpleAdapter);

        try {
            task.execute("http://a98k98k.dothome.co.kr/user_reg.php?userID=" + userID);
        } catch (Exception e) {
            e.printStackTrace();
            task.cancel(true);
            task = new phpDown();
            task.execute("http://a98k98k.dothome.co.kr/user_reg.php?userID=" + userID);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleAdapter.notifyDataSetChanged();
            }
        });


        list.setOnItemClickListener(this);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_reg.this, mypage.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        HashMap<String, String> c_list = data.get(i);
        String userID = getIntent().getStringExtra("userID");
        Intent intent = new Intent(user_reg.this, info.class);
        intent.putExtra("marker", Integer.parseInt(c_list.get("item 1")));
        intent.putExtra("userID", userID);
        intent.putExtra("user_reg", 1);
        startActivity(intent);
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
                        item.setaddress(infoObject.getString("address"));
                        item.setno(infoObject.getString("no"));
                        list_item = new HashMap<String,String>();
                        list_item.put("item 1", item.getno());
                        list_item.put("item 2", item.getaddress());
                        data.add(list_item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException jsonException) {

            }

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