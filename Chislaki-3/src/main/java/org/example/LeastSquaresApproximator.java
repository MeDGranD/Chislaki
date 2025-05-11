package org.example;

import lombok.Getter;
import org.example.Interpolation.InterpolationPoint;
import org.example.Utils.LUResolver;

import java.util.List;

public class LeastSquaresApproximator {

    private final List<InterpolationPoint> points;
    private final int degree;
    @Getter
    private double[] coefs = null;

    public LeastSquaresApproximator(
            List<InterpolationPoint> points,
            int degree
    ){
        this.points = points;
        this.degree = degree;
        solve();
    }

    public double evaluate(double x){

        double result = 0;
        for (int i = degree - 1; i >= 0; i--) {
            result = result * x + coefs[i];
        }
        return result;

    }

    public double squareError(){

        double sumSqr = 0.0;
        for (InterpolationPoint point : points) {
            double xi = point.getX();
            double yi = point.getY();
            double pi = evaluate(xi);
            sumSqr += Math.pow(yi - pi, 2);
        }
        return sumSqr;

    }

    private void solve(){

        double[][] ACoefs = new double[degree][degree];
        double[] BCoefs = new double[degree];

        for(int i = 0; i < degree; ++i){
            for(int j = 0; j < degree; ++j){
                for(var point : points)
                    ACoefs[i][j] += Math.pow(point.getX(), i + j);
            }
            for(var point : points)
                BCoefs[i] += point.getY() * Math.pow(point.getX(), i);
        }

        LUResolver resolver = new LUResolver(ACoefs);
        coefs = resolver.solveLU(BCoefs).toArray();

    }

}
