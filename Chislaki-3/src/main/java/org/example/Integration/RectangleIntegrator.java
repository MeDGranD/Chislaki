package org.example.Integration;

import java.util.function.Function;

public class RectangleIntegrator implements NumericalIntegrator{

    @Override
    public double integrate(Function<Double, Double> func, double a, double b, double h) {

        double sum = 0.0;
        if(a > b){
            throw new IllegalArgumentException("a cannot be bigger than b.");
        }

        while(a <= b){
            sum += func.apply((2 * a - h) / 2);
            a += h;

            if(a > b && a - h != b){
                sum += func.apply((2 * b - h) / 2);
                break;
            }

        }

        return h * sum;
    }

}
