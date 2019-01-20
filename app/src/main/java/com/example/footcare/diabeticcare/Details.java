package com.example.footcare.diabeticcare;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.core.Mat;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Details extends AppCompatActivity {
    DatabaseHelper myDB = new DatabaseHelper(this);
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        long Date = getIntent().getLongExtra("Date", 0);
        Date itemDate = new Date(Date);

        buttonHandler(Date);

        Object[] imageData = fetchImageData(itemDate);
        String notes = imageData[0].toString();
        Uri imageUri = Uri.parse(imageData[1].toString());

        String woundPercent = fetchWoundPercent(itemDate);

        TextView textView= (TextView) this.findViewById(R.id.textView);
        textView.setText(notes);

        TextView dateText= (TextView) this.findViewById(R.id.dateText);
        dateText.setText(df.format(itemDate));

        TextView percent= (TextView) this.findViewById(R.id.percent);
        percent.setText(woundPercent+"%");

        ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        progressBar.setProgress(Integer.parseInt(woundPercent));

        mImageView=(ImageView)findViewById(R.id.imageView);
        mImageView.setImageURI(imageUri);

        }


    public Object[] fetchImageData(Date date) {
        Cursor res = myDB.getAllData(DatabaseHelper.TABLE_IMAGES);
        Object[] results = new Object[2];

        while (res.moveToNext()) {
            Uri theUri = Uri.parse(res.getString(2));
            if(theUri != null && theUri.getPath() != ""){
                try {
                    java.util.Date itemDate = new java.text.SimpleDateFormat("dd/MM/yy").parse(res.getString(5));
                    if (Objects.equals(itemDate, date)) {
                        results[0] = res.getString(4);
                        results[1] = theUri;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return results;
    }

    public String fetchWoundPercent(Date date) {
        Cursor res = myDB.getAllData(DatabaseHelper.TABLE_ANALYSIS);
        String result = "";
        DecimalFormat df = new DecimalFormat("#.##");

        while (res.moveToNext()) {
            if (!Objects.equals(res.getString(1), null)) {
                if (!Objects.equals(res.getString(1), "")) {
                    try {
                        java.util.Date itemDate = new java.text.SimpleDateFormat("dd/MM/yy").parse(res.getString(4));
                        if (Objects.equals(itemDate, date)) {
//                            result = res.getString(1);
                            int resultInt = (int) Double.parseDouble(res.getString(1));
                            result = Integer.toString(resultInt);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public int fetchEntriesLength() {
        Cursor res = myDB.getAllData(DatabaseHelper.TABLE_ANALYSIS);
        int i = 0;
        while (res.moveToNext()) {
            if (!Objects.equals(res.getString(1), null)) {
                if (!Objects.equals(res.getString(1), "")) {
                    i++;
                }
            }
        }
        return i;
    }

    public long[] createDateArray(int length) {
        Cursor res = myDB.getAllData(DatabaseHelper.TABLE_ANALYSIS);
        long[] entryDates = new long[length];

        int i = 0;
        while (res.moveToNext()) {
            if (!Objects.equals(res.getString(1), null)) {
                if (!Objects.equals(res.getString(1), "")) {
                    try {
                        java.util.Date itemDate = new java.text.SimpleDateFormat("dd/MM/yy").parse(res.getString(4));
                        long millis = itemDate.getTime();
                        entryDates[i] = millis;
                        i++;

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return entryDates;
    }

    public void buttonHandler(long Date){
        int length = fetchEntriesLength();
        final long[] entryDates = createDateArray(length);
        int currentDateInt = 0;
        for (int i = 0; i < entryDates.length;){
            if(Date == entryDates[i]){
                currentDateInt = i;
            }
            i++;
        }

        Button back = (Button) this.findViewById(R.id.back);
        Button next = (Button) this.findViewById(R.id.next);

        if(currentDateInt == 0){
            back.setVisibility(View.INVISIBLE);
        }

        if(currentDateInt == length-1){
            next.setVisibility(View.INVISIBLE);
        }

        final int finalCurrentDateInt = currentDateInt;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Details.class);
                intent.putExtra("Date", entryDates[finalCurrentDateInt-1]);
                startActivity(intent);
                overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_right );
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Details.class);
                intent.putExtra("Date", entryDates[finalCurrentDateInt+1]);
                startActivity(intent);
                overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left );
                finish();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }
}

