package com.kelto.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class Calculator {

    public Integer[] getDivisors(int number) {
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
