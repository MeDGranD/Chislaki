package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class NewtonSolver{

    private static Function<Double, Double> differentiate(Function<Double, Double> f){
        return x -> (f.apply(x + 1e-5) - f.apply(x - 1e-5)) / (2 * 1e-5);
    }

    public static double getRoot( //TODO: проверять монотонность в границах и другие условия существования корня
            Function<Double, Double> func,
            double start,
            double tolerance,
            int maxIter
    ){

        if(func.apply(start) * differentiate(differentiate(func)).apply(start) <= 0){
            System.out.println("Возможно ответ в Ньютоне не сойдется");
        }

        double xVal = start;
        double error = Double.MAX_VALUE;
        int iter = 0;

        while(error > tolerance || iter++ < maxIter){

            double oldX = xVal;
            xVal = xVal - func.apply(xVal) / differentiate(func).apply(xVal);

            error = Math.abs(oldX - xVal);

        }

        return xVal;

    }

}
