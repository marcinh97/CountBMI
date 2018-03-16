package com.example.marcin.bmi_app.BMI;

public class BmiPoundsFeet extends Bmi {
    private static final double MIN_MASS = 10;
    private static final double MAX_MASS = 1000;
    private static final double MIN_HEIGHT = 20;
    private static final double MAX_HEIGHT = 120;
    private static final double BMI_CONVERSION_RATE_KGS_TO_LBS = 703;

    public BmiPoundsFeet(double mass, double height) {
        super(mass, height);
        minMass = MIN_MASS;
        maxMass = MAX_MASS;
        minHeight = MIN_HEIGHT;
        maxHeight = MAX_HEIGHT;
    }

    @Override
    public double countBmi() throws IllegalArgumentException {
        validateInput();
        return (getMass() / (getHeight()*getHeight())) * BMI_CONVERSION_RATE_KGS_TO_LBS;
    }

}
