package com.kelto.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class Calculator {

    private static final Logger LOGGER = Logger.getLogger(Calculator.class.getName());

    public Integer[] getDivisors(int number) {

        LOGGER.log(Level.INFO,"Getdivisors called");
        List<Integer> divisors = new ArrayList<Integer>();
        int divisor = number / 2;
        while (divisor > 1) {
            if(number % divisor == 0) {
                divisors.add(divisor);
            }
            divisor--;
        }


        return divisors.toArray(new Integer[divisors.size()]);
    }
}
