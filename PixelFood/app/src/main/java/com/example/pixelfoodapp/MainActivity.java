package com.example.pixelfoodapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.InputStreamEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    private static final String Upload_URL = "http://192.168.43.98/";
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        requestStoragePermission();
        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        final Button btnCamera = (Button)findViewById(R.id.btnCamera);
        final Button btnUpload = (Button)findViewById(R.id.btnUpload);
        final Button btnSeeAnalysis = (Button)findViewById(R.id.btnSeeData);
        btnSeeAnalysis.setEnabled(false);
        imageView = (ImageView)findViewById(R.id.imageView);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
                btnSeeAnalysis.setEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0){
            super.onActivityResult(requestCode, resultCode, data);
            // Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");


            // String NameOfFolder = "FoodPixel";
            String NameOfFile = "FoodPixelFood";
            //String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+NameOfFolder;
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String currentDateAndTime = getCurrentDateAndTime();
            //File directory = new File(file_path);

//                if(!directory.exists()){
//                    directory.mkdirs();
//                }

            File imFile = new File(pictureDirectory, NameOfFile + currentDateAndTime + ".png");

            Uri pictureUri = Uri.fromFile(imFile);
            // intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

            OutputStream out = null;

            try{
                out = new FileOutputStream(imFile);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                out.flush();
                out.close();
            } catch (java.io.IOException e){
                e.printStackTrace();
            }

//            try {
//                HttpClient httpclient = new DefaultHttpClient();
//
//                HttpPost httppost = new HttpPost(Upload_URL);
//
//                InputStreamEntity reqEntity = new InputStreamEntity(
//                        new FileInputStream(imFile), -1);
//                reqEntity.setContentType("binary/octet-stream");
//                reqEntity.setChunked(true); // Send in multiple parts if needed
//                httppost.setEntity(reqEntity);
//                //HttpResponse response = httpclient.execute(httppost);
//                //Do something with response...
//
//            } catch (Exception e) {
//                // show error
//            }

            imageView.setImageBitmap(bitmap);

        }

        else if (requestCode == 3){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadImage(filePath);
           // Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            //imageView.setImageBitmap(bitmap);
        }


    }

    private String getCurrentDateAndTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = s.format(c.getTime());
        return formattedDate;
    }

    private String getPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null,null,null,null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,MediaStore.Images.Media._ID + "=?", new String[]{document_id},null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 123) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadImage(Uri filePath){
        String name = "upload image";
        String path = getPath(filePath);

        //HttpURLConnection connection = null;
        String urlParameters = "1000";
        System.out.println("Before");
        try {

            System.out.println("Before");
            URL url = new URL(Upload_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestMethod("POST");
            //connection.setRequestProperty("Content-Type" "application/x-www-form-urlencoded");
            connection.setRequestMethod("GET");
            System.out.println(connection.getPermission());
            //connection.setDoOutput(true);
            //connection.setConnectTimeout(5000);
            //connection.setReadTimeout(5000);
            //connection.connect();
            //BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //String content = "", line;
//            while ((line = rd.readLine()) != null) {
//                content += line + "\n";
//            }
            //HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //con.setRequestMethod("GET");
           // int responsecode = connection.getResponseCode();
            //System.out.println(responsecode);
            System.out.println("Apres");
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type",
//                    "application/x-www-form-urlencoded");
//
//            connection.setRequestProperty("Content-Length",
//                    Integer.toString(urlParameters.getBytes().length));
//            connection.setRequestProperty("Content-Language", "en-US");
//
//            connection.setUseCaches(false);
//            connection.setDoOutput(true);
//
//            //Send request
//            DataOutputStream wr = new DataOutputStream (
//                    connection.getOutputStream());
//            wr.writeBytes(urlParameters);
//            wr.close();
//
//            //Get Response
//            InputStream is = connection.getInputStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
//            //response.append("Hi");
//            String line;
//            while ((line = rd.readLine()) != null) {
//                response.append(line);
//                response.append('\r');
//            }
//            rd.close();
//            //return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            //return null;
        }
//        finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
    }

    public void onClickData(View view) {
        Intent showDataScreenIntent = new Intent(this, SecondScreen.class);
        showDataScreenIntent.putExtra("Data", "Blah for now");
        startActivity(showDataScreenIntent);
    }
}
