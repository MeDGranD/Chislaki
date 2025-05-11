package org.example.Interpolation;

import lombok.NonNull;

import java.util.List;

public class NewtonInterpolator implements Interpolator{

    @NonNull
    private final List<InterpolationPoint> points;
    private final double[] dividedDifferences;


    public NewtonInterpolator(List<InterpolationPoint> points) {
        this.points = points;
        this.dividedDifferences = calculateDividedDifferences();
    }

    private double[] calculateDividedDifferences() {

        int n = points.size();
        double[][] table = new double[n][n];

        for (int i = 0; i < n; i++) {
            table[0][i] = points.get(i).getY();
        }

        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                double denominator = points.get(i).getX() - points.get(i + j).getX();
                if (Math.abs(denominator) < 1e-15) {
                    throw new ArithmeticException("Duplicate x values found in interpolation points at index " + i + " and " + j + ". Cannot compute divided differences.");
                }
                table[j][i] = (table[j - 1][i] - table[j - 1][i + 1]) / denominator;
            }
        }

        double[] coefficients = new double[n];
        for (int i = 0; i < n; i++) {
            coefficients[i] = table[i][0];
        }
        return coefficients;
    }

    @Override
    public double interpolate(double x) {

        int n = points.size();

        double newtonPolynomialValue = dividedDifferences[0];
        double termProduct = 1.0;

        for (int k = 1; k < n; k++) {
            termProduct *= (x - points.get(k - 1).getX());
            newtonPolynomialValue += dividedDifferences[k] * termProduct;
        }

        return newtonPolynomialValue;
    }

    @Override
    public void printPolynomial() {
        StringBuilder polynomial = new StringBuilder();
        int n = points.size();

        for (int i = 0; i < n; i++) {
            double coefficient = dividedDifferences[i];

            if (Math.abs(coefficient) < 1e-12) continue;

            if (i > 0) {
                polynomial.append(" + ");
            }

            polynomial.append(String.format("%.4f", coefficient));

            for (int j = 0; j < i; j++) {
                double xj = points.get(j).getX();
                polynomial.append(String.format(" * (x - %.4f)", xj));
            }
        }

        System.out.println("Newton interpolation polynomial:");
        System.out.println(polynomial);
    }

}
