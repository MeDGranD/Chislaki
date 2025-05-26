package org.example.Integration;

import java.util.function.Function;

public class TrapezoidIntegrator implements NumericalIntegrator{

    @Override
    public double integrate(Function<Double, Double> func, double a, double b, double h) {
        double sum = 0.0, oldA = a;
        a += h;
        if(a > b){
            throw new IllegalArgumentException("a cannot be bigger than b.");
        }

        while(a <= b){
            sum += func.apply(a) + func.apply(a - h);
            a += h;

            if(a > b && a - h != b){
                throw new IllegalArgumentException(String.format("Указан неправильный шаг: нельзя целочисленно разбить промежуток [%f, %f] на шаги %f", oldA, b, h));
            }

        }

        return h * sum / 2;
    }

}
