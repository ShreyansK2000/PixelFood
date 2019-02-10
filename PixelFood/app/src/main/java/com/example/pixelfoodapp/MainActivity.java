package com.example.pixelfoodapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.InputStreamEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class MainActivity extends AppCompatActivity {

    int Camera_Request_Code = 7;
    int Upload_Request_Code = 77;
    int WRITE_Request_Code = 1;
    int READ_Request_Code = 2;

    ImageView imageView;
    private static final String Upload_URL = "http://192.168.43.98/";
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_Request_Code);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_Request_Code);

        final Button btnCamera = (Button)findViewById(R.id.btnCamera);
        final Button btnUpload = (Button)findViewById(R.id.btnUpload);
        final Button btnSeeAnalysis = (Button)findViewById(R.id.btnSeeData);
        imageView = (ImageView)findViewById(R.id.imageView);

        btnSeeAnalysis.setEnabled(false);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCam = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intentCam, Camera_Request_Code );
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentUpload = new Intent();
                intentUpload.setType("image/*");
                intentUpload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentUpload, "Select Picture"), Upload_Request_Code);

                btnSeeAnalysis.setEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == Camera_Request_Code && resultCode == RESULT_OK){
            super.onActivityResult(requestCode, resultCode, data);
            Bitmap bitmapIm = (Bitmap)data.getExtras().get("data");

            String NameOfFile = "FoodPixelFood";
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            String currentDateAndTime = getCurrentDateAndTime();
            File imFile = new File(pictureDirectory, NameOfFile + currentDateAndTime + ".png");

            try{
                OutputStream out = new FileOutputStream(imFile);
                bitmapIm.compress(Bitmap.CompressFormat.PNG,100,out);
                out.flush();
                out.close();
            } catch (java.io.IOException e){
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmapIm);

        }

        if (requestCode == Upload_Request_Code){
            filePath = data.getData();
            JSONObject jsonObj = new JSONObject();
            try {
                Bitmap chosenImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(chosenImage);

                String encodedImage = getStringFromBitmap(chosenImage);
                jsonObj = new JSONObject("{\"image\":\" + encodedImage + \"}");

            } catch (Exception e) {
                e.printStackTrace();
            }
            //SendData send = new SendData();
            //send.onPostExecute();
           // executeWrite(Upload_URL, jsonObj);
        }
    }

    private String getCurrentDateAndTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formatDate = date.format(cal.getTime());
        return formatDate;
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
           int responsecode = connection.getResponseCode();
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
        showDataScreenIntent.putExtra("Data", "Blah for now @ Blah 2 for now @ blah 3 for now");
        startActivity(showDataScreenIntent);
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        /*
         * This functions converts Bitmap picture to a string which can be
         * JSONified.
         * */
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }
    /*
    public void executeWrite(String requestUrl, JSONObject jsonObject)
    {
        InputStreamReader  input = null;
        try
        {
            URL                 url;
            HttpURLConnection urlConn;
            DataOutputStream printout;

            System.out.println(requestUrl);
            // URL of CGI-Bin script.
            url = new URL (requestUrl);
            // URL connection channel.
            urlConn = (HttpURLConnection)url.openConnection();
            // Let the run-time system (RTS) know that we want input.
            urlConn.setDoInput (true);
            // Let the RTS know that we want to do output.
            urlConn.setDoOutput (true);
            // No caching, we want the real thing.
            urlConn.setUseCaches (false);
            // Specify the content type.
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("content-type","application/json; charset=utf-8");

            OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
            wr.write(jsonObject.toString());
            wr.flush();
            wr.close();

            input = new InputStreamReader (urlConn.getInputStream ());
            String response = UserInterface.read(new BufferedReader(input));

            if(response.length() > 0)
            {
                System.out.println("Response:" + response);
            }

            input.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }*/
}

class SendData extends AsyncTask<String, Void, Boolean> {
    DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    Date date = new Date();
    String datefinal = dateFormat.format(date).toString();
    String url = "https://192.168.43.98/send/";


    @Override
    protected Boolean doInBackground(String... urls) {
        try{
            HttpPost httppost = new HttpPost(url);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);

            // StatusLine stat = response.getStatusLine();
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);

                JSONObject jsono = new JSONObject(data);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return false;
    }
    protected void onPostExecute(Boolean result) {

    }
}