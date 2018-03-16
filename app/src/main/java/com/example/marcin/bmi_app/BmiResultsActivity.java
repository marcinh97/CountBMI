package com.example.marcin.bmi_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class BmiResultsActivity extends AppCompatActivity {

    TextView bmiResults;
    ImageButton backToPreviousActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_results);
        double result = 0;

        Intent intent = getIntent();
        result = intent.getDoubleExtra(MainActivity.BMI_RESULT, result); // tu ostatnia zmiana, z 

        bmiResults = findViewById(R.id.bmi_results_text_view);
        bmiResults.setText(String.format(Locale.ENGLISH,"%.2f", result));

        backToPreviousActivity = findViewById(R.id.back_to_prev_activity_button);
        backToPreviousActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
