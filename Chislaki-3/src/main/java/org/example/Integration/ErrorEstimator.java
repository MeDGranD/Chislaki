package org.example.Integration;

public class ErrorEstimator {

    public static double estimateRungeRombergError(double I_h1, double I_h2, double k, double p) {
        double denominator = Math.pow(k, p) - 1.0;
        if (Math.abs(denominator) < 1e-15) {
            throw new ArithmeticException("Denominator (r^p - 1) is close to zero, cannot apply Runge-Romberg reliably.");
        }
        return I_h1 + (I_h1 - I_h2) / denominator;
    }

}
