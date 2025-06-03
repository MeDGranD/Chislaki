package org.example.Interpolation;

import lombok.NonNull;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.example.Utils.TriagonalResolver;

import java.util.List;

public class CubicSplineInterpolator implements Interpolator{

    @NonNull
    List<InterpolationPoint> points;
    private double[] h;
    private double[] a;
    private double[] b;
    private double[] c;
    private double[] d;

    public CubicSplineInterpolator(List<InterpolationPoint> points) {

        this.points = points;

        int n = points.size() - 1;
        this.h = new double[n];
        for (int i = 0; i < n; i++) {
            this.h[i] = points.get(i + 1).getX() - points.get(i).getX();
            if (this.h[i] <= 0) {
                throw new IllegalArgumentException("X values must be strictly increasing.");
            }
        }

        findCoefs();
    }

    private void findCoefs(){

        int n = h.length - 1;

        double[][] ACoefs = new double[n][n];
        double[] BCoefs = new double[n];

        for(int i = 0; i < n; ++i){
            for(int j = 0; j < n; ++j){
                if(j < i - 1 || j > i + 1){
                    continue;
                }
                else if(i == j){
                    ACoefs[i][j] = 2 * (h[i] + h[i + 1]);
                }
                else{
                    ACoefs[i][j] = h[i];
                }
            }
            BCoefs[i] = 3 * ((points.get(i + 2).getY() - points.get(i + 1).getY()) / h[i + 1] - (points.get(i + 1).getY() - points.get(i).getY()) / h[i]);
        }

        RealVector cValue = TriagonalResolver.solveTridiagonal(new Array2DRowRealMatrix(ACoefs), BCoefs);

        c = new double[n + 1];
        a = new double[n + 1];
        b = new double[n + 1];
        d = new double[n + 1];

        for(int i = 0; i < n + 1; ++i){
            if(i == 0){
                c[i] = 0;
            }
            else{
                c[i] = cValue.getEntry(i - 1);
            }
        }

        for(int i = 0; i < n + 1; ++i) {
            a[i] = points.get(i).getY();
            if(i == n){
                b[i] = (points.get(i + 1).getY() - points.get(i).getY()) / h[i] - (2d / 3) * h[i] * c[i];
                d[i] = -c[i] / (3 * h[i]);
            }
            else{
                b[i] = (points.get(i + 1).getY() - points.get(i).getY()) / h[i] - (1d / 3) * h[i] * (c[i + 1] + 2 * c[i]);
                d[i] = (c[i + 1] - c[i]) / (3 * h[i]);
            }
        }

    }

    @Override
    public double interpolate(double x) { //TODO: Убрать значение вне отрезка

        if(x < points.getFirst().getX() || x > points.getLast().getX())
            throw new IllegalArgumentException(String.format("Значение x должно быть в пределах от %f до %f", points.getFirst().getX(), points.getLast().getX()));

        int i;
            for (i = 0; i < points.size() - 1; i++) {
                double left = points.get(i).getX();
                double right = points.get(i + 1).getX();
                if (x >= left && x <= right) {
                    break;
                }
            }

        return a[i] + b[i] * (x - points.get(i).getX()) + c[i] * Math.pow((x - points.get(i).getX()), 2) + d[i] * Math.pow((x - points.get(i).getX()), 3);
    }

    @Override
    public void printPolynomial() { //TODO: Построить графики
        System.out.println("Cubic spline interpolation polynomials (one per interval):");

        for (int i = 0; i < h.length; i++) {
            double xi = points.get(i).getX();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Interval [%.4f, %.4f]: ", xi, points.get(i + 1).getX()));
            sb.append(String.format("S_%d(x) = ", i));
            sb.append(String.format("%.4f", a[i]));

            //if (Math.abs(b[i]) > 1e-12)
                sb.append(String.format(" + %.4f*(x - %.4f)", b[i], xi));
            //if (Math.abs(c[i]) > 1e-12)
                sb.append(String.format(" + %.4f*(x - %.4f)^2", c[i], xi));
            //if (Math.abs(d[i]) > 1e-12)
                sb.append(String.format(" + %.4f*(x - %.4f)^3", d[i], xi));

            System.out.println(sb);
        }
    }
}
