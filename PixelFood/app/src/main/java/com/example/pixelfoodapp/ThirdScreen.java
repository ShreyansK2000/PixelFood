package com.example.pixelfoodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ThirdScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.third_layout);

        Intent dataCollect = getIntent();
        String data = dataCollect.getExtras().getString("Data");

        TextView dataDisplay = (TextView) findViewById(R.id.idData);
        dataDisplay.append(" " + data);
    }

    public void onClickReturn(View view) {
        Intent goingBack = new Intent();
        //goingBack.putExtra("Main");
        setResult(RESULT_OK, goingBack);

        finish();
    }
}
