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
        double oldVal = func.apply(a) - a;
        double sgn = Math.signum(func.apply(a+1e-5) - (a+1e-5) - (func.apply(a) - a));
        double q = 0;
        double startA = a;

        if(Math.signum(oldVal) + Math.signum(func.apply(b) - b) != 0)
            throw new RuntimeException("Для данных условий задачи не существует единственный корень");

        while(a <= b){

            double val = func.apply(a);
            if(val < startA || val > b){
                System.err.println("Возможно ответ в простых итерациях не сойдется");
            }

            double nulVal = func.apply(a) - a;
            if(Double.isNaN(nulVal)){
                throw new RuntimeException("Заданная функция не имеет значения на заданном промежутке");
            }
            if(a != startA && sgn*(nulVal - oldVal) < 0){
                throw new RuntimeException("Для данных условий задачи не существует единственный корень");
            }

            q = Math.max(Math.abs(differentiate(func).apply(a)), q); //Всегда выводить в конце

            a += step;
        }

        if(q >= 1) {
            System.err.println("Возможно ответ в простых итерациях не сойдется");
        }


        double error = Double.MAX_VALUE;
        double xVal = start;
        int iter = 0;

        while(error > tolerance && iter++ < maxIter){

            double oldX = xVal;
            xVal = func.apply(xVal);

            error = (q / (1 - q)) * Math.abs(oldX - xVal);

        }

        System.out.printf("Корень был найден за %d итераций с конечной ошибкой %f\nq = %f\n", iter, error, q);
        return xVal;

    }
}
