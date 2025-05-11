package org.example;

import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SystemNewtonSolver{

    private static Function<double[], Double> differentiate(Function<double[], Double> f, int i) {
        return point -> {
            double[] xhPlus = point.clone();
            double[] xhMinus = point.clone();

            xhPlus[i] += 1e-5;
            xhMinus[i] -= 1e-5;

            double fPlus = f.apply(xhPlus);
            double fMinus = f.apply(xhMinus);

            return (fPlus - fMinus) / (2 * 1e-5);
        };
    }

    public static double[] getRoots(
            double[] start,
            Function<double[], Double>[] funcs,
            double tolerance,
            int maxIter
    ){

        @SuppressWarnings("unchecked")
        Function<double[], Double>[][] jacobian = (Function<double[], Double>[][]) new Function[2][2];

        jacobian[0][0] = differentiate(funcs[0], 0);
        jacobian[0][1] = differentiate(funcs[0], 1);
        jacobian[1][0] = differentiate(funcs[1], 0);
        jacobian[1][1] = differentiate(funcs[1], 1);

        DecompositionSolver solver;

        double error = Double.MAX_VALUE;
        int iter = 0;

        while(error > tolerance && iter++ < maxIter){

            double[][] data = new double[funcs.length][funcs.length];
            double[] bPart = new double[funcs.length];

            for(int i = 0; i < funcs.length; ++i){
                for(int j = 0; j < funcs.length; ++j){
                    data[i][j] = jacobian[i][j].apply(start);
                }
                bPart[i] = -funcs[i].apply(start);
            }

            solver = new LUDecomposition(new Array2DRowRealMatrix(data)).getSolver();
            RealVector deltas = solver.solve(new ArrayRealVector(bPart));

            for(int i = 0; i < funcs.length; ++i){
                start[i] = start[i] + deltas.toArray()[i];
            }

            error = deltas.getLInfNorm();

        }

        return start;

    }

}
