package com.example.marcin.bmi_app;

import com.example.marcin.bmi_app.BMI.Bmi;
import com.example.marcin.bmi_app.BMI.BmiKgMeters;
import com.example.marcin.bmi_app.BMI.BmiPoundsFeet;

import org.junit.Test;

import static org.junit.Assert.*;

public class BmiPoundsFeetTest {

    @Test
    public void counts_bmi_with_pounds_inches_correctly(){
        double mass = 150; // pounds
        double height = 78; // inches
        double expectedValue = 17.33;
        Bmi bmi = new BmiPoundsFeet(mass, height);
        assertEquals(bmi.countBmi(), expectedValue, 0.01);
    }

    @Test(expected = Bmi.WrongMassHeightException.class)
    public void wrong_mass_height_exception_thrown(){
        double mass = 1200;
        double height = 1500;
        Bmi bmi = new BmiKgMeters(mass, height);
        bmi.countBmi();
    }

    @Test(expected = Bmi.WrongMassException.class)
    public void wrong_mass_exception_thrown(){
        double mass = -10;
        double height = 1.80;
        Bmi bmi = new BmiKgMeters(mass, height);
        bmi.countBmi();
    }

    @Test(expected = Bmi.WrongHeightException.class)
    public void wrong_height_exception_thrown(){
        double mass = 120;
        double height = 0;
        Bmi bmi = new BmiKgMeters(mass, height);
        bmi.countBmi();
    }

}
