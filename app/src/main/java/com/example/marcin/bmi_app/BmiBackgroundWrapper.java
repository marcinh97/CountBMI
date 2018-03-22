package com.example.marcin.bmi_app;

/**
 * Created by marcin on 22.03.2018.
 */

public class BmiBackgroundWrapper {
    private double bmiLevel;
    private static final double UNDERWEIGHT_UPPER_BMI = 18.5;
    private static final double NORMAL_UPPER_BMI = 25;
    private static final double OVERWEIGHT_UPPER_BMI = 30;

    BmiBackgroundWrapper(double bmiLevel){
        this.bmiLevel = bmiLevel;
    }

    public int getBackgroundDependingOnBmi(){
        if (bmiLevel < UNDERWEIGHT_UPPER_BMI){
            return R.drawable.underweight_background;
        }
        else if (bmiLevel < NORMAL_UPPER_BMI){
            return R.drawable.normal_weight_background;
        }
        else if (bmiLevel < OVERWEIGHT_UPPER_BMI){
            return R.drawable.overweight_background;
        }
        return R.drawable.obesity_background;
    }


}
