package com.example.marcin.bmi_app;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class BmiResultsActivity extends AppCompatActivity {

    public static final String BMI_RESULTS_FORMAT = "%.2f";
    private TextView bmiResults;
    private ImageButton backToPreviousActivity;
    private ConstraintLayout layout;
    private double result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_results);
        result = 0;

        Intent intent = getIntent();
        result = intent.getDoubleExtra(MainActivity.BMI_RESULT, result);

        initViews();
        initListeners();
        setLayoutDetails();
    }

    private void initViews(){
        bmiResults = findViewById(R.id.bmi_results_text_view);
        backToPreviousActivity = findViewById(R.id.back_to_prev_activity_button);
        layout = findViewById(R.id.activity_bmi_results_id);
    }

    private void initListeners(){
        backToPreviousActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setLayoutDetails(){
        bmiResults.setText(String.format(Locale.ENGLISH, BMI_RESULTS_FORMAT, result));
        int backgroundId = new BmiBackgroundWrapper(result).getBackgroundDependingOnBmi();
        layout.setBackground(ContextCompat.getDrawable(this, backgroundId));
    }
    

}
