package com.example.marcin.bmi_app.BMI;



public class BmiKgMeters extends Bmi {
    private static final double MIN_MASS = 5;
    private static final double MAX_MASS = 500;
    private static final double MIN_HEIGHT = 0.5;
    private static final double MAX_HEIGHT = 3;

    public BmiKgMeters(double mass, double height){
        super(mass, height);
        minMass = MIN_MASS;
        maxMass = MAX_MASS;
        minHeight = MIN_HEIGHT;
        maxHeight = MAX_HEIGHT;
    }


    @Override
    public double countBmi() throws IllegalArgumentException{
        validateInput();
        return getMass() / (getHeight()*getHeight());
    }

}
