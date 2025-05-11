package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class SimpleIterationSolver{

    private static final int MON_ITER = 1000;

    private static Function<Double, Double> differentiate(Function<Double, Double> f){
        return x -> (f.apply(x + 1e-5) - f.apply(x - 1e-5)) / (2 * 1e-5);
    }

    public static double getRoot(
            Function<Double, Double> func,
            double a,
            double b,
            double start,
            double tolerance,
            int maxIter
    ){

        double step = (b - a) / MON_ITER;
        double q = 0;
        double startA = a;

        while(a <= b){

            double val = func.apply(a);
            if(val < startA || val > b){
                throw new RuntimeException("Error");
            }

            q = Math.max(Math.abs(differentiate(func).apply(a)), q);

            a += step;
        }

        if(q >= 1) {
            throw new RuntimeException("Error");
        }


        double error = Double.MAX_VALUE;
        double xVal = start;
        int iter = 0;

        while(error > tolerance && iter++ < maxIter){

            double oldX = xVal;
            xVal = func.apply(xVal);

            error = (q / (1 - q)) * Math.abs(oldX - xVal);

        }

        return xVal;

    }
}
