package com.example.marcin.bmi_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import com.example.marcin.bmi_app.BMI.Bmi;
import com.example.marcin.bmi_app.BMI.BmiKgMeters;
import com.example.marcin.bmi_app.BMI.BmiPoundsFeet;

public class MainActivity extends AppCompatActivity {

    public static final String MASS_KEY = "mass";
    public static final String HEIGHT_KEY = "height";
    public static final String BMI_RESULT = "bmiResult";
    public static final String MASS_HINT = "massHint";
    public static final String HEIGHT_HINT = "heightHint";
    public static final String SWITCH_STATUS = "switchStatus";
    public static final String MASS_INPUT_FORMAT = "%.0f";

    private Switch unitChanger; // isChecked() = lbs
    private EditText massInput;
    private EditText heightInput;
    private SharedPreferences sharedPreferences;

    private boolean isPopupShown = false;
    public static final String IS_POPUP_SHOWN = "isPopupShown";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        unitChanger = findViewById(R.id.change_units_switch);
        unitChanger.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getApplicationContext(), getText(R.string.switch_description_long), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        massInput = findViewById(R.id.mass_edit_text);
        heightInput = findViewById(R.id.height_edit_text);

        retrieveMassHeightIfAvailableAndDisplayInTextViews();

        unitChanger.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (unitChanger.isChecked()) {
                    massInput.setHint(R.string.pounds);
                    heightInput.setHint(R.string.inches);
                } else {
                    massInput.setHint(R.string.kilograms);
                    heightInput.setHint(R.string.meters);
                }
            }
        });

        Button countBmiButton = findViewById(R.id.count_bmi_button);
        countBmiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countBmi();
            }
        });

        ImageView saveIcon = findViewById(R.id.save_icon);
        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMassHeightToRetrieveLater();
            }
        });

       /* if (savedInstanceState != null){
            if (savedInstanceState.containsKey(IS_POPUP_SHOWN)){
                if (savedInstanceState.getBoolean(IS_POPUP_SHOWN)){
                    makeLongToastWithMessage("hehe");
                }
            }
        }*/

    }

    private void saveMassHeightToRetrieveLater() {
        double[] values = new double[2];
        boolean hasErrors = false;
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            values = parseMassHeight();
        } catch (Bmi.NoArgumentsException e){
            hasErrors = true;
            makeLongToastWithMessage(getText(R.string.saved_values_deleted).toString());
            editor.clear();
            editor.apply();
        } catch (IllegalArgumentException e) {
            hasErrors = true;
            showErrors(e);
        }
        if (!hasErrors) {
            double mass = values[0];
            double height = values[1];

            editor.putLong(MASS_KEY, Double.doubleToRawLongBits(mass));
            editor.putLong(HEIGHT_KEY, Double.doubleToRawLongBits(height));
            editor.putBoolean(SWITCH_STATUS, unitChanger.isChecked());
            makeLongToastWithMessage(getText(R.string.successful_save_message).toString());
            editor.apply();
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void retrieveMassHeightIfAvailableAndDisplayInTextViews(){
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        double mass, height;
        boolean bothValuesRetrievable = sharedPreferences.contains(MASS_KEY) && sharedPreferences.contains(HEIGHT_KEY);
        boolean switchStatus;

        if (bothValuesRetrievable){
            mass = Double.longBitsToDouble(sharedPreferences.getLong(MASS_KEY, 0));
            height = Double.longBitsToDouble(sharedPreferences.getLong(HEIGHT_KEY, 0));
            switchStatus = sharedPreferences.getBoolean(SWITCH_STATUS, false);

            String massString = Double.toString(mass);
            String heightString = Double.toString(height);

            mass = Double.parseDouble(massString.replace(',','.'));
            height = Double.parseDouble(heightString.replace(',','.'));

            massInput.setText(String.format(MASS_INPUT_FORMAT, mass));
            // massInput.setText(Double.toString(mass));
            heightInput.setText(Double.toString(height));
            unitChanger.setChecked(switchStatus);

            if (switchStatus){ // on = lbs
                massInput.setHint(getText(R.string.pounds).toString());
                heightInput.setHint(getText(R.string.inches).toString());
            }
        }

    }

    private double[] parseMassHeight() throws IllegalArgumentException{
        double[] values = new double[2];
        String massToParse = massInput.getText().toString();
        String heightToParse = heightInput.getText().toString();
        if (isMassHeightInputEmpty(massToParse, heightToParse)){
            throw new Bmi.NoArgumentsException();
        }
        double mass = Double.parseDouble(massToParse);
        double height = Double.parseDouble(heightToParse);
        values[0] = mass;
        values[1] = height;
        return values;
    }

    private void countBmi() {
        double[] massAndHeight = new double[2];
        boolean hasErrors = false;
        try {
            massAndHeight = parseMassHeight();
        } catch (IllegalArgumentException e) {
            showErrors(e);
            hasErrors = true;
        }
        if (!hasErrors){
            double mass = massAndHeight[0];
            double height = massAndHeight[1];
            double result = 0;
            hasErrors = false;
            Bmi bmiCounter = unitChanger.isChecked() ? new BmiPoundsFeet(mass, height) : new BmiKgMeters(mass, height);
            try {
                result = bmiCounter.countBmi();
            } catch (IllegalArgumentException e) {
                showErrors(e);
                hasErrors = true;
            } finally {
                if (!hasErrors) {
                    Intent intent = new Intent(this, BmiResultsActivity.class);
                    intent.putExtra(BMI_RESULT, result);
                    startActivity(intent);
                }
            }
        }
    }

    private void showErrors(IllegalArgumentException e){
        String errorMessage;
        if (e.getClass().equals(Bmi.WrongMassException.class)){
            errorMessage = getText(R.string.wrong_mass_error_message).toString();
        } else if (e.getClass().equals(Bmi.WrongHeightException.class)){
            errorMessage = getText(R.string.wrong_height_error_message).toString();

        } else if (e.getClass().equals(Bmi.WrongMassHeightException.class)){
            errorMessage = getText(R.string.wrong_mass_height_error_message).toString();

        } else if (e.getClass().equals(Bmi.NoArgumentsException.class)){
            errorMessage = getText(R.string.empty_input_error_message).toString();
        }
        else{
            errorMessage = getText(R.string.unknown_error_message).toString();
        }
        makeLongToastWithMessage(errorMessage);
    }

    private boolean isMassHeightInputEmpty(String massToParse, String heightToParse){
        return massToParse.isEmpty() || heightToParse.isEmpty();
    }

    private void makeLongToastWithMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about_author:
                showAuthorImagePopup();
                break;
            default:
                Toast.makeText(getApplicationContext(), getText(R.string.unknown_error_message), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void showAuthorImagePopup(){
        final double POPUP_WINDOW_RATIO = 0.7;
        final float SHADOW_BEHIND_POPUP_LEVEL = 10;

        final PopupWindow authorImagePopup;
        LayoutInflater layoutInflater; // new layout inside window
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") ViewGroup container = (ViewGroup) (layoutInflater != null ? layoutInflater.inflate(R.layout.author_info, null) : null);

        int orientation = getResources().getConfiguration().orientation;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        authorImagePopup = new PopupWindow(container, (int)(POPUP_WINDOW_RATIO *displayMetrics.widthPixels),
                (int)(POPUP_WINDOW_RATIO *displayMetrics.heightPixels), true);
        authorImagePopup.setElevation(SHADOW_BEHIND_POPUP_LEVEL); // shadow behind popup

        final ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);

        final int bottomPositionOfImageCenter = orientation == Configuration.ORIENTATION_LANDSCAPE ?
                (int)(getResources().getDimension(R.dimen.bottom_position_of_image_center)) : 0;

        authorImagePopup.showAtLocation(constraintLayout, Gravity.CENTER, 0, bottomPositionOfImageCenter);

        isPopupShown = true;

        assert container != null;
        container.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                authorImagePopup.dismiss();
                isPopupShown = false;
                return true;
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String massHint = massInput.getHint().toString();
        String heightHint = heightInput.getHint().toString();
        outState.putString(MASS_HINT, massHint);
        outState.putString(HEIGHT_HINT, heightHint);
        outState.putBoolean(IS_POPUP_SHOWN, isPopupShown);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String massHint = savedInstanceState.getString(MASS_HINT);
        String heightHint = savedInstanceState.getString(HEIGHT_HINT);
        massInput.setHint(massHint);
        heightInput.setHint(heightHint);
    }

}
