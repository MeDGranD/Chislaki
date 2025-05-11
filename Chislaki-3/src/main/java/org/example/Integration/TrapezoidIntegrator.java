package org.example.Integration;

import java.util.function.Function;

public class TrapezoidIntegrator implements NumericalIntegrator{

    @Override
    public double integrate(Function<Double, Double> func, double a, double b, double h) {
        double sum = 0.0;
        a += h;
        if(a > b){
            throw new IllegalArgumentException("a cannot be bigger than b.");
        }

        while(a <= b){
            sum += func.apply(a) + func.apply(a - h);
            a += h;

            if(a > b && a - h != b){
                sum += func.apply(b) + func.apply(b - h);
                break;
            }

        }

        return h * sum / 2;
    }

}
