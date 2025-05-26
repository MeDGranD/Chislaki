package org.example;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SystemSimpleIterationSolver{

    private static final int MON_ITER = 1000;

    public static Function<double[], Double> partialDerivative(Function<double[], Double> function, int variableIndex) {
        double h = 1e-5;

        return (double[] x) -> {
            double[] xPlus = x.clone();
            double[] xMinus = x.clone();
            xPlus[variableIndex] += h;
            xMinus[variableIndex] -= h;
            return (function.apply(xPlus) - function.apply(xMinus)) / (2 * h);
        };
    }

    public static double[] getRoots( //TODO: добавить q и проверки
            Function<double[], Double>[] phi_functions,
            double[] a,
            double[] b,
            double[] initialGuess,
            double tolerance,
            int maxIter
    ){

        Function<double[], Double>[][] jacobian = new Function[phi_functions.length][phi_functions.length];
        for(int i = 0; i < phi_functions.length; ++i){
            for(int j = 0; j < phi_functions.length; ++j){
                jacobian[i][j] = partialDerivative(phi_functions[i], j);
            }
        }

        double stepA = (a[1] - a[0]) / MON_ITER;
        double stepB = (b[1] - b[0]) / MON_ITER;
        double currentA = a[0], currentB = b[0];
        double q = 0;

        while(currentA <= a[1]){
            while(currentB <= b[1]){

                double[][] val = new double[phi_functions.length][phi_functions.length];
                for(int i = 0; i < phi_functions.length; ++i){
                    for(int j = 0; j < phi_functions.length; ++j){
                        val[i][j] = jacobian[i][j].apply(new double[]{currentA, currentB});
                    }
                }

                q = Math.max(
                        q,
                        new Array2DRowRealMatrix(val).getNorm()
                );

                currentB += stepB;
            }
            currentA += stepA;
        }

        if(q >= 1-1e8) {
            System.err.println("Возможно ответ в простых итерациях не сойдется");
        }

        double error = Double.MAX_VALUE;
        int iter = 0;
        int n = initialGuess.length;

        double[] xCurrent = Arrays.copyOf(initialGuess, n);
        double[] xPrevious = new double[n];


        while (error > tolerance && iter++ < maxIter) {
            System.arraycopy(xCurrent, 0, xPrevious, 0, n);

            double[] xNext = new double[n];
            for (int i = 0; i < n; ++i) {
                xNext[i] = phi_functions[i].apply(xPrevious);
            }
            System.arraycopy(xNext, 0, xCurrent, 0, n);

            double[] diffVector = new double[n];
            for (int i = 0; i < n; i++) {
                diffVector[i] = xCurrent[i] - xPrevious[i];
            }
            error = new ArrayRealVector(diffVector).getLInfNorm() * (q/(1-q));

        }

        System.out.printf("Корень был найден за %d итераций с конечной ошибкой %f\nq = %f\n", iter, error, q);
        return xCurrent;

    }

}