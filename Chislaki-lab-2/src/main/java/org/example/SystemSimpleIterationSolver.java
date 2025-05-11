package org.example;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SystemSimpleIterationSolver{

    public static double[] getRoots(
            Function<double[], Double>[] phi_functions,
            double[] initialGuess,
            double tolerance,
            int maxIter
    ){

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
            error = new ArrayRealVector(diffVector).getLInfNorm();

        }

        return xCurrent;

    }

}