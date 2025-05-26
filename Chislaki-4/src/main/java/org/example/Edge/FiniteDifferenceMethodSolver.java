package org.example.Edge;

import org.apache.commons.math3.linear.*;
import org.example.SolutionPoint;

import java.util.ArrayList;
import java.util.List;

public class FiniteDifferenceMethodSolver {
    private static final double x_start = 1.0;
    private static final double x_end = 2.0;

    private static double p(double x) { return -(2 * x + 1) / x; }
    private static double q(double x) { return (2 * x + 1) / x; }
    private static double f(double x) { return 0.0; }

    public static List<SolutionPoint> solve(double h) {
        int N = (int) Math.round((x_end - x_start) / h);
        if (N < 2) {
            System.err.println("FDM: h is too large. N must be at least 2.");
            return null;
        }
        int numUnknowns = N + 1;

        double[][] matrixA_data = new double[numUnknowns][numUnknowns];
        double[] vectorB_data = new double[numUnknowns];

        matrixA_data[0][0] = -3.0;
        if (numUnknowns > 1) matrixA_data[0][1] = 4.0;
        if (numUnknowns > 2) matrixA_data[0][2] = -1.0;
        vectorB_data[0] = 6 * h * Math.E;

        for (int i = 1; i < N; i++) {
            double xi = x_start + i * h;
            matrixA_data[i][i - 1] = 1.0 - (h / 2.0) * p(xi);
            matrixA_data[i][i]     = -2.0 + h * h * q(xi);
            matrixA_data[i][i + 1] = 1.0 + (h / 2.0) * p(xi);
            vectorB_data[i] = h * h * f(xi);
        }

        if (numUnknowns >= 3) matrixA_data[N][N - 2] = 1.0;
        if (numUnknowns >= 2) matrixA_data[N][N - 1] = -4.0;
        matrixA_data[N][N]     = 3.0 - 4.0 * h;
        vectorB_data[N] = 0.0;

        RealMatrix matrixA = new Array2DRowRealMatrix(matrixA_data);
        DecompositionSolver solver = new LUDecomposition(matrixA).getSolver();

        RealVector y_values = solver.solve(new ArrayRealVector(vectorB_data));

        if (y_values == null) {
            System.err.println("FDM: Failed to solve linear system.");
            return null;
        }

        List<SolutionPoint> result = new ArrayList<>();
        for (int i = 0; i <= N; i++) {
            double xi = x_start + i * h;
            double yi = y_values.getEntry(i);
            double yi_prime;
            if (i == 0) {
                if (N < 2) yi_prime = Double.NaN;
                else yi_prime = (-3 * y_values.getEntry(0) + 4 * y_values.getEntry(1) - y_values.getEntry(2)) / (2 * h);
            } else if (i == N) {
                if (N < 2) yi_prime = Double.NaN;
                else yi_prime = (3 * y_values.getEntry(N) - 4 * y_values.getEntry(N - 1) + y_values.getEntry(N - 2)) / (2 * h);
            } else {
                yi_prime = (y_values.getEntry(i + 1) - y_values.getEntry(i - 1)) / (2 * h);
            }
            result.add(new SolutionPoint(xi, new double[]{yi, yi_prime}));
        }
        return result;
    }

}
