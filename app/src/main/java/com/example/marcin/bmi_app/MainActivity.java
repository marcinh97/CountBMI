package com.example.marcin.bmi_app;

import android.annotation.SuppressLint;
import android.content.Context;
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
    public static final String MASS_HINT = "massHint";
    public static final String HEIGHT_HINT = "heightHint";
    public static final String SWITCH_STATUS = "switchStatus";
    public static final String IS_POPUP_SHOWN = "isPopupShown";

    private Switch unitChanger; // isChecked() = lbs
    private EditText massInput;
    private EditText heightInput;
    private Button countBmiButton;
    private ImageView saveValuesIcon;
    private ImageView deleteValuesIcon;

    private SharedPreferences sharedPreferences;

    private boolean isPopupShown = false;

    private PopupWindow authorImagePopup;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        initViews();
        initListeners();

        retrieveSavedMassHeight();
    }

    private void initViews(){
        unitChanger = findViewById(R.id.change_units_switch);
        massInput = findViewById(R.id.mass_edit_text);
        heightInput = findViewById(R.id.height_edit_text);
        countBmiButton = findViewById(R.id.count_bmi_button);
        saveValuesIcon = findViewById(R.id.save_icon);
        deleteValuesIcon = findViewById(R.id.delete_saved_values_button);
    }

    private void initListeners(){
        unitChanger.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return showAdditionalInfoForSwitch();
            }
        });

        unitChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInputWhenSwitchPressed(unitChanger.isChecked());
            }
        });

        countBmiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countBmi();
            }
        });

        saveValuesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMassHeightToRetrieveLater();
            }
        });

        deleteValuesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSavedValues();
                makeLongToastWithMessage(getText(R.string.saved_values_deleted).toString());
            }
        });
    }

    private void deleteSavedValues() {
        sharedPreferences.edit().clear().apply();
        deleteValuesInTextViews();
    }

    private void deleteValuesInTextViews(){
        massInput.setText("");
        heightInput.setText("");
    }

    private boolean showAdditionalInfoForSwitch(){
        makeLongToastWithMessage(getText(R.string.switch_description_long).toString());
        return true;
    }

    private void setInputWhenSwitchPressed(boolean isChecked){
        deleteValuesInTextViews();
        String massMessage = isChecked ? getString(R.string.pounds) : getString(R.string.kilograms);
        String heightMessage = isChecked ? getString(R.string.inches) : getString(R.string.meters);
        massInput.setHint(massMessage);
        heightInput.setHint(heightMessage);
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
                    BmiResultsActivity.start(this, result);
                }
            }
        }
    }

    private void saveMassHeightToRetrieveLater(){
        String[] values = new String[2];
        boolean hasErrors = false;
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            values = getInitialMassHeightInput();
        }
        catch (IllegalArgumentException e) {
            hasErrors = true;
            showErrors(e);
        }
        if (!hasErrors){
            String mass = values[0];
            String height = values[1];
            editor.putString(MASS_KEY, mass);
            editor.putString(HEIGHT_KEY, height);
            editor.putBoolean(SWITCH_STATUS, unitChanger.isChecked());
            makeLongToastWithMessage(getText(R.string.successful_save_message).toString());
            editor.apply();
        }
    }

    private void retrieveSavedMassHeight(){
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String mass, height;
        boolean bothValuesRetrievable = sharedPreferences.contains(MASS_KEY) && sharedPreferences.contains(HEIGHT_KEY);
        boolean switchStatus;

        if(bothValuesRetrievable){
            mass = sharedPreferences.getString(MASS_KEY, "").replace(',','.');
            height = sharedPreferences.getString(HEIGHT_KEY, "").replace(',','.');
            switchStatus = sharedPreferences.getBoolean(SWITCH_STATUS, false);

            massInput.setText(mass);
            heightInput.setText(height);
            unitChanger.setChecked(switchStatus);
            if (switchStatus){
                massInput.setHint(getText(R.string.pounds).toString());
                heightInput.setHint(getText(R.string.inches).toString());
            }
        }
    }

    private double[] parseMassHeight() throws IllegalArgumentException{
        String[] valuesToParse = getInitialMassHeightInput();
        double mass = Double.parseDouble(valuesToParse[0]);
        double height = Double.parseDouble(valuesToParse[1]);
        return new double[]{mass, height};
    }

    private String[] getInitialMassHeightInput() throws IllegalArgumentException{
        String massToParse = massInput.getText().toString();
        String heightToParse = heightInput.getText().toString();
        if (isMassHeightInputEmpty(massToParse, heightToParse)){
            throw new Bmi.NoArgumentsException();
        }
        return new String[]{massToParse, heightToParse};
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
        final ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        int orientation = getResources().getConfiguration().orientation;
        final int bottomPositionOfImageCenter = orientation == Configuration.ORIENTATION_LANDSCAPE ?
                (int)(getResources().getDimension(R.dimen.bottom_position_of_image_center)) : 0;

        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") ViewGroup container = (ViewGroup) (layoutInflater != null ?
                layoutInflater.inflate(R.layout.author_info, null) : null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        authorImagePopup = new PopupWindow(container, (int)(POPUP_WINDOW_RATIO *displayMetrics.widthPixels),
                (int)(POPUP_WINDOW_RATIO *displayMetrics.heightPixels), true);
        authorImagePopup.setElevation(SHADOW_BEHIND_POPUP_LEVEL);

        showAuthorImagePopupOnScreen(constraintLayout, bottomPositionOfImageCenter);

        allowUserToDismissPopupByClicking(container);
    }

    private void showAuthorImagePopupOnScreen(ConstraintLayout layout, int positionOfImage){
        authorImagePopup.showAtLocation(layout, Gravity.CENTER, 0, positionOfImage);
        isPopupShown = true;
    }

    private void allowUserToDismissPopupByClicking(ViewGroup container){
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
