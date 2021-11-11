package com.example.everytown;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class room_reg1 extends AppCompatActivity implements View.OnClickListener {

    private final String host = "ip";
    private final String username = "id";
    private final String password = "pw";
    private final int port = 21;
    private final String TAG = "Connect FTP";
    private ConnectFTP ConnectFTP;

    private ImageButton back;
    private Button contract;
    private Button upload;
    private EditText et_add;
    private EditText et_area;
    private EditText et_opt;
    private EditText et_perFi;
    private EditText et_perSe;
    private EditText et_pri;
    private EditText et_memo;

    private long backKeyPressedTime = 0;
    private Toast toast;

    phpDown task;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    private ImageView imgMain;
    private Button btnCamera, btnAlbum;
    private Uri photoUri;
    File Photofile = null;

    private MediaScannerConnection mMediaScanner;
    private MediaScannerConnection.MediaScannerConnectionClient mMediaScannerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_reg);
        back = findViewById(R.id.back);
        contract = findViewById(R.id.contract);
        upload = findViewById(R.id.upload);

        et_add = findViewById(R.id.et_add);
        et_area = findViewById(R.id.et_area);
        et_opt = findViewById(R.id.et_opt);
        et_perFi = findViewById(R.id.et_perFi);
        et_perSe = findViewById(R.id.et_perSe);
        et_pri = findViewById(R.id.et_pri);
        et_memo = findViewById(R.id.et_memo);


        task = new phpDown();

        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(room_reg1.this, law.class);
                intent.putExtra("userID", getIntent().getStringExtra("userID"));
                intent.putExtra("law", 1);
                startActivity(intent);
            }
        });
        contract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = getIntent().getStringExtra("userID");
                String address = et_add.getText().toString();
                String area = et_area.getText().toString();
                String userOption = et_opt.getText().toString();
                String periodFirst = et_perFi.getText().toString();
                String periodSecond = et_perSe.getText().toString();
                String price = et_pri.getText().toString();
                String memo = et_memo.getText().toString();
                String filename = "없음";
                if (Photofile!=null)
                    filename = Photofile.getName();

                if (userID.equals("")||address.equals("")||area.equals("")||userOption.equals("")||periodFirst.equals("")||periodSecond.equals("")||price.equals("")||memo.equals("")){
                    Toast.makeText(getApplicationContext(), "빈칸을 다 채워주세요!", Toast.LENGTH_SHORT).show();
                }else {

                    try {
                        List<Address> addresses = geocoder.getFromLocationName(address, 3);

                        StringBuffer buffer = new StringBuffer();
                        for (Address t : addresses) {
                            buffer.append(t.getLatitude() + ", " + t.getLongitude() + "\n");
                        }
                        String latitude = String.format("%.9f", addresses.get(0).getLatitude());
                        String longitude = String.format("%.9f", addresses.get(0).getLongitude());
                        try {
                            try {
                                task.execute("http://a98k98k.dothome.co.kr/info_save.php?userID=" + userID + "&address=" + address + "&area=" + area + "&userOption=" + userOption + "&periodFirst=" + periodFirst + "&periodSecond=" + periodSecond + "&price=" + price + "&memo=" + memo + "&latitude=" + latitude + "&longitude=" + longitude + "&photo=" + filename);
                                Toast.makeText(getApplicationContext(), "방 등록에 성공했습니다!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(room_reg1.this, MainActivity.class);
                                intent.putExtra("userID", getIntent().getStringExtra("userID"));
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                task.cancel(true);
                                task = new phpDown();
                                task.execute("http://a98k98k.dothome.co.kr/info_save.php?userID=" + userID + "&address=" + address + "&area=" + area + "&userOption=" + userOption + "&periodFirst=" + periodFirst + "&periodSecond=" + periodSecond + "&price=" + price + "&memo=" + memo + "&latitude=" + latitude + "&longitude=" + longitude + "&photo=" + filename);
                                Toast.makeText(getApplicationContext(), "방 등록에 성공했습니다!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(room_reg1.this, MainActivity.class);
                                intent.putExtra("userID", getIntent().getStringExtra("userID"));
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "방 등록에 실패했습니다!", Toast.LENGTH_SHORT).show();
                            task.cancel(true);
                            task = new phpDown();
                        }
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "검색 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        initView();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            ConnectFTP.ftpUploadFile(Photofile.getPath(),Photofile.getName());
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
                Toast.makeText(getApplicationContext(), "업로드 되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        imgMain = findViewById(R.id.img_test);
        btnCamera = findViewById(R.id.btn_camera);
        btnAlbum = findViewById(R.id.btn_album);

        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(room_reg1.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(room_reg1.this,
                    "com.example.everytown", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "nostest_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile( imageFileName, ".jpg", storageDir);
        Photofile = image;
        return image;
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                takePhoto();
                break;
            case R.id.btn_album:
                goToAlbum();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            MediaScannerConnection.scanFile(room_reg1.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            imgMain.setImageURI(null);
            imgMain.setImageURI(photoUri);
            revokeUriPermission(photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

    public void cropImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
            intent.putExtra("crop", "true");
            intent.putExtra("outputX", 1200);
            intent.putExtra("outputY", 800);
            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 2);
            intent.putExtra("scale", true);

            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());
            photoUri = FileProvider.getUriForFile(room_reg1.this,
                    "com.example.everytown", tempFile);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            grantUriPermission(res.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);

        }
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
