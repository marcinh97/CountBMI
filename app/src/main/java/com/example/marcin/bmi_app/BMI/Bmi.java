package com.example.marcin.bmi_app.BMI;

public abstract class Bmi {
    double minMass;
    double maxMass;
    double minHeight;
    double maxHeight;
    private double mass;
    private double height;


    public static class WrongMassHeightException extends IllegalArgumentException{
        WrongMassHeightException(){
            super();
        }
    }
    public static class WrongMassException extends IllegalArgumentException{
        WrongMassException() {
        }
    }
    public static class WrongHeightException extends IllegalArgumentException{
        WrongHeightException() {
        }
    }
    public static class NoArgumentsException extends IllegalArgumentException{
        public NoArgumentsException() {
        }
    }

    Bmi(double mass, double height) {
        this.mass = mass;
        this.height = height;
    }

    public abstract double countBmi() throws IllegalArgumentException;
    void validateInput() throws IllegalArgumentException{
        boolean isMassWrong = getMass() < minMass || getMass() > maxMass;
        boolean isHeightWrong = getHeight() < minHeight || getHeight() > maxHeight;

        if (isMassWrong){
            if (isHeightWrong){
                throw new Bmi.WrongMassHeightException();
            }
            else{
                throw new Bmi.WrongMassException();
            }
        }
        else{
            if (isHeightWrong){
                throw new Bmi.WrongHeightException();
            }
        }
    }

    public double getMass() {
        return mass;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

}
