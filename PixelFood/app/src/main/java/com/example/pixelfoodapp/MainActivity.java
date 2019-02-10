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
import java.util.HashMap;
import java.util.Map;
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
            File imFile = new File(pictureDirectory, NameOfFile + currentDateAndTime + ".jpg");

            if(!imFile.exists()){
                imFile.mkdir();
            }

            try{
                OutputStream out = new FileOutputStream(imFile);
                bitmapIm.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.flush();
                out.close();
            } catch (java.io.IOException e){
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmapIm);

        }

        if (requestCode == Upload_Request_Code){
            filePath = data.getData();
            try {
                Bitmap chosenImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(chosenImage);

            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadImage(filePath);
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

/*
    /**
     * Demonstrates using the AutoML client to predict an image.
     *
     * @param projectId the Id of the project.
     * @param computeRegion the Region name.
     * @param modelId the Id of the model which will be used for text classification.
     * @param filePath the Local text file path of the content to be classified.
     * @param scoreThreshold the Confidence score. Only classifications with confidence score above
     *     scoreThreshold are displayed.
     * @throws IOException on Input/Output errors.
     *
    public static void predict(
            String projectId,
            String computeRegion,
            String modelId,
            String filePath,
            String scoreThreshold)
            throws IOException {

        project_id = 'pixelfood';
        compute_region = 'us-central1';
        model_id = 'ICN4832559605394156485';
        score_threshold = '0.7';
        key = 'pixelfood-e2b93b66a82f.json';

        // Instantiate client for prediction service.
        PredictionServiceClient predictionClient = PredictionServiceClient.create();

        // Get the full path of the model.
        ModelName name = ModelName.of(projectId, computeRegion, modelId);

        // Read the image and assign to payload.
        ByteString content = ByteString.copyFrom(Files.readAllBytes(Paths.get(filePath)));
        Image image = Image.newBuilder().setImageBytes(content).build();
        ExamplePayload examplePayload = ExamplePayload.newBuilder().setImage(image).build();

        // Additional parameters that can be provided for prediction e.g. Score Threshold
        Map<String, String> params = new HashMap<>();
        if (scoreThreshold != null) {
            params.put("score_threshold", scoreThreshold);
        }
        // Perform the AutoML Prediction request
        PredictResponse response = predictionClient.predict(name, examplePayload, params);

        System.out.println("Prediction results:");
        for (AnnotationPayload annotationPayload : response.getPayloadList()) {
            System.out.println("Predicted class name :" + annotationPayload.getDisplayName());
            System.out.println(
                    "Predicted class score :" + annotationPayload.getClassification().getScore());
        }
    } */
}
