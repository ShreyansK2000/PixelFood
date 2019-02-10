package com.example.pixelfoodapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.imageView);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
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
        // Uri pictureUri = Uri.fromFile(imFile);
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
        imageView.setImageBitmap(bitmap);

    }

    private String getCurrentDateAndTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = s.format(c.getTime());
        return formattedDate;
    }
    }
