package com.example.marcin.bmi_app;

import com.example.marcin.bmi_app.BMI.Bmi;
import com.example.marcin.bmi_app.BMI.BmiKgMeters;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BmiKgMetersUnitTest {

    @Test
    public void counts_bmi_correctly(){
        double mass = 70;
        double height = 1.80;
        double expectedValue = 21.605;
        Bmi bmi = new BmiKgMeters(mass, height);
        assertEquals(bmi.countBmi(), expectedValue, 0.001);
    }

   @Test(expected = Bmi.WrongMassHeightException.class)
    public void wrong_mass_height_exception_thrown(){
        double mass = -10;
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