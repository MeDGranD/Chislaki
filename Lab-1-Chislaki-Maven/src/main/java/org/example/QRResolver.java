package org.example;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.linear.*;

import java.util.*;

public class QRResolver {

    public static class QRResult {
        public RealMatrix Q;
        public RealMatrix R;

        public QRResult(RealMatrix Q, RealMatrix R) {
            this.Q = Q;
            this.R = R;
        }
    }

    private static double norm(RealVector x, int i){
        double norm = 0;
        for(; i < x.getDimension(); ++i){
            norm += x.getEntry(i)*x.getEntry(i);
        }
        return Math.sqrt(norm);
    }

    public static RealVector householderVector(RealVector x, int start) {

        double norm = norm(x, start);
        RealVector v = new ArrayRealVector(x.getDimension(), 0);
        v.setEntry(start,
                x.getEntry(start) + Math.signum(x.getEntry(start)) * norm
        );
        ++start;
        for(; start < v.getDimension(); ++start){
            v.setEntry(start, x.getEntry(start));
        }

        return v;
    }

    public static QRResult householderQR(RealMatrix A) {

        int m = A.getRowDimension();
        RealMatrix R = A.copy();
        RealMatrix Q = MatrixUtils.createRealIdentityMatrix(m);

        for (int k = 0; k < m; k++) {
            RealVector x = R.getColumnVector(k);
            RealVector v = householderVector(x, k);

            RealMatrix H = MatrixUtils.createRealIdentityMatrix(m).subtract(
                    v.outerProduct(v).scalarMultiply(2 / v.dotProduct(v))
            );

            R = H.multiply(R);
            Q = Q.multiply(H);
        }

        return new QRResult(Q, R);
    }

    public static Complex[] getRoots(RealMatrix A, int i) {
        int n = A.getRowDimension();

        double a11 = A.getEntry(i, i);
        double a12 = (i + 1 < n) ? A.getEntry(i, i + 1) : 0;
        double a21 = (i + 1 < n) ? A.getEntry(i + 1, i) : 0;
        double a22 = (i + 1 < n) ? A.getEntry(i + 1, i + 1) : 0;

        // Характеристический многочлен: x^2 - (a11 + a22)x + (a11*a22 - a12*a21)
        double b = -(a11 + a22);
        double c = a11 * a22 - a12 * a21;

        double discriminant = b * b - 4 * c;

        if (discriminant >= 0) {
            double sqrtD = Math.sqrt(discriminant);
            return new Complex[] {
                    new Complex((-b + sqrtD) / 2),
                    new Complex((-b - sqrtD) / 2)
            };
        } else {
            double real = -b / 2;
            double imag = Math.sqrt(-discriminant) / 2;
            return new Complex[] {
                    new Complex(real, imag),
                    new Complex(real, -imag)
            };
        }
    }

    public static boolean isComplex(RealMatrix A, int i, double eps) {
        QRResult qr = householderQR(A);
        RealMatrix A_next = qr.R.multiply(qr.Q);

        Complex[] lambda1 = getRoots(A, i);
        Complex[] lambda2 = getRoots(A_next, i);

        return lambda1[0].subtract(lambda2[0]).abs() <= eps &&
                lambda1[1].subtract(lambda2[1]).abs() <= eps;
    }

    public static Object[] getEigenValue(RealMatrix A, int i, double eps) {
        RealMatrix A_i = A.copy();
        int n = A_i.getRowDimension();

        while (true) {
            QRResult qr = householderQR(A_i);
            A_i = qr.R.multiply(qr.Q);

            double normBelow = 0;
            for (int row = i + 1; row < n; row++) {
                normBelow += Math.pow(A_i.getEntry(row, i), 2);
            }

            if (Math.sqrt(normBelow) <= eps) {
                return new Object[] { new Complex(A_i.getEntry(i, i)), A_i };
            }

            double normFarBelow = 0;
            for (int row = i + 2; row < n; row++) {
                normFarBelow += Math.pow(A_i.getEntry(row, i), 2);
            }

            if (Math.sqrt(normFarBelow) <= eps && isComplex(A_i, i, eps)) {
                return new Object[] { getRoots(A_i, i), A_i };
            }
        }
    }

    public static List<Complex> getEigenValuesQR(RealMatrix A, double eps) {
        int n = A.getRowDimension();
        RealMatrix A_i = A.copy();

        List<Complex> eigenValues = new ArrayList<>();
        int i = 0;

        while (i < n) {
            Object[] result = getEigenValue(A_i, i, eps);
            Object curEigen = result[0];
            A_i = (RealMatrix) result[1];

            if (curEigen instanceof Complex[]) {
                Complex[] lambdas = (Complex[]) curEigen;
                eigenValues.add(lambdas[0]);
                eigenValues.add(lambdas[1]);
                i += 2;
            } else {
                eigenValues.add((Complex) curEigen);
                i += 1;
            }
        }

        return eigenValues;
    }



}
