package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class NewtonSolver{

    private static final int MON_ITER = 1000;

    private static Function<Double, Double> differentiate(Function<Double, Double> f){
        return x -> (f.apply(x + 1e-5) - f.apply(x - 1e-5)) / (2 * 1e-5);
    }

    public static double getRoot( //TODO: проверять монотонность в границах и условия существования корня
            Function<Double, Double> func,
            double a,
            double b,
            double start,
            double tolerance,
            int maxIter
    ){

        double step = (b - a) / MON_ITER;
        double oldVal = func.apply(a);
        double sgn = Math.signum(func.apply(a+1e-5) - func.apply(a));
        double startA = a;

        if(Math.signum(oldVal) + Math.signum(func.apply(b)) != 0)
            throw new RuntimeException("Для данных условий задачи не существует единственный корень");

        while(a <= b){

            double nulVal = func.apply(a);
            if(Double.isNaN(nulVal)){
                throw new RuntimeException("Заданная функция не имеет значения на заданном промежутке");
            }
            if(a != startA && sgn*(nulVal - oldVal) < 0){
                throw new RuntimeException("Для данных условий задачи не существует единственный корень");
            }

            a += step;

        }

        if(func.apply(start) * differentiate(differentiate(func)).apply(start) <= 0){
            System.err.println("Возможно ответ в Ньютоне не сойдется");
        }

        double xVal = start;
        double error = Double.MAX_VALUE;
        int iter = 0;

        while(error > tolerance && iter++ < maxIter){

            double oldX = xVal;
            xVal = xVal - func.apply(xVal) / differentiate(func).apply(xVal);

            error = Math.abs(oldX - xVal);

        }

        System.out.printf("Корень был найден за %d итераций с конечной ошибкой %f\n", iter, error);
        return xVal;

    }

}
