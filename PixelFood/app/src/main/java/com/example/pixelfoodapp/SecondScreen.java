package com.example.pixelfoodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SecondScreen extends Activity {

    public String[] datasets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second_layout);

        Intent dataCollect = getIntent();
        String data = dataCollect.getExtras().getString("Data");
        datasets = data.split("@");
    }

    public void onClickReturn(View view) {
        Intent goingBack = new Intent();
        //goingBack.putExtra("Main");
        setResult(RESULT_OK, goingBack);

        finish();
    }

    public void onClickCal(View view) {
        Intent showDataScreenIntent = new Intent(this, ThirdScreen.class);
        showDataScreenIntent.putExtra("Data", datasets[0]);
        startActivity(showDataScreenIntent);
    }

    public void onClickDAlegens(View view) {
        Intent showDataScreenIntent = new Intent(this, ThirdScreen.class);
        showDataScreenIntent.putExtra("Data", datasets[1]);
        startActivity(showDataScreenIntent);
    }

    public void onClicIngredients(View view) {
        Intent showDataScreenIntent = new Intent(this, ThirdScreen.class);
        showDataScreenIntent.putExtra("Data", datasets[2]);
        startActivity(showDataScreenIntent);
    }
}
