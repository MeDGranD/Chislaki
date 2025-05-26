package org.example;

import lombok.NonNull;
import org.example.Interpolation.InterpolationPoint;
import org.example.Interpolation.NewtonInterpolator;

import java.util.List;
import java.util.function.Function;

public class NumericDifferentiator {

    @NonNull
    private final List<InterpolationPoint> points;
    private final NewtonInterpolator interpolator;

    private static Function<Double, Double> differentiate(Function<Double, Double> f){
        return x -> (f.apply(x + 1e-5) - f.apply(x - 1e-5)) / (2 * 1e-5);
    }

    public NumericDifferentiator(List<InterpolationPoint> points){
        this.points = points;
        if(points.size() < 3)
            throw new IllegalArgumentException("Points array must be greater than 3");
        for (int i = 0; i < points.size() - 1; i++) {
            double h = points.get(i + 1).getX() - points.get(i).getX();
            if (h <= 0) {
                throw new IllegalArgumentException("X values must be strictly increasing.");
            }
        }
        this.interpolator = new NewtonInterpolator(points);
    }

    private int findNodeIndex(double x) {
        int i = 0;
        if(x < points.getFirst().getX()){
            throw new IllegalArgumentException("Out of range");
        }
        else if(x > points.getLast().getX()){
            throw new IllegalArgumentException("Out of range");
        }
        else{
            for (; i < points.size() - 1; i++) {
                double left = points.get(i).getX();
                double right = points.get(i + 1).getX();
                if (x >= left && x <= right) {
                    break;
                }
            }
        }
        return i;
    }

    public double[] calculateAtNode(double x) { //TODO: объяснить выводы = подписи

        /*return new double[] {
                differentiate(interpolator::interpolate).apply(x),
                differentiate(differentiate((interpolator::interpolate))).apply(x)
        };*/

        double y_i, y_i_plus_1, y_i_minus_1, y_i_plus_2, y_i_minus_2;
        double x_i, x_i_plus_1, x_i_minus_1, x_i_plus_2, x_i_minus_2;

        int i = findNodeIndex(x);

        if(points.size() == 3){
            if(i == 0){

                y_i = points.get(i).getY();
                y_i_plus_1 = points.get(i + 1).getY();


                x_i = points.get(i).getX();
                x_i_plus_1 = points.get(i + 1).getX();

                return new double[]{(y_i_plus_1 - y_i) / (x_i_plus_1 - x_i)};
            }
            else{
                y_i = points.get(i).getY();
                y_i_minus_1 = points.get(i - 1).getY();


                x_i = points.get(i).getX();
                x_i_minus_1 = points.get(i - 1).getX();

                return new double[]{(y_i_minus_1 - y_i) / (x_i_minus_1 - x_i)};
            }
        }

        if(i == 0){

            y_i = points.get(i).getY();
            y_i_plus_1 = points.get(i + 1).getY();


            x_i = points.get(i).getX();
            x_i_plus_1 = points.get(i + 1).getX();

            x_i_plus_2 = points.get(i + 2).getX();
            y_i_plus_2 = points.get(i + 2).getY();

            double v = (y_i_plus_2 - y_i_plus_1) / (x_i_plus_2 - x_i_plus_1) - (y_i_plus_1 - y_i) / (x_i_plus_1 - x_i);

            double firstDerivative = (y_i_plus_1 - y_i) / (x_i_plus_1 - x_i) + v / (x_i_plus_2 - x_i) * (2 * x - x_i - x_i_plus_1);
            double secondDerivative = 2 * v / (x_i_plus_2 - x_i);

            return new double[]{firstDerivative, secondDerivative};

        }
        else if(i == points.size() - 1){
            y_i = points.get(i).getY();
            y_i_minus_1 = points.get(i - 1).getY();


            x_i = points.get(i).getX();
            x_i_minus_1 = points.get(i - 1).getX();

            x_i_minus_2 = points.get(i - 2).getX();
            y_i_minus_2 = points.get(i - 2).getY();

            double v = (y_i - y_i_minus_1) / (x_i - x_i_minus_1) - (y_i_minus_1 - y_i_minus_2) / (x_i_minus_1 - x_i_minus_2);

            double firstDerivative = (y_i_minus_1 - y_i_minus_2) / (x_i_minus_1 - x_i_minus_2) + v / (x_i - x_i_minus_2) * (2 * x - x_i_minus_2 - x_i_minus_1);
            double secondDerivative = 2 * v / (x_i - x_i_minus_2);

            return new double[]{firstDerivative, secondDerivative};
        }
        else{
            y_i = points.get(i).getY();
            y_i_minus_1 = points.get(i - 1).getY();


            x_i = points.get(i).getX();
            x_i_minus_1 = points.get(i - 1).getX();

            x_i_plus_1 = points.get(i + 1).getX();
            y_i_plus_1 = points.get(i + 1).getY();

            double v = (y_i_plus_1 - y_i) / (x_i_plus_1 - x_i) - (y_i - y_i_minus_1) / (x_i - x_i_minus_1);

            double firstDerivative = (y_i - y_i_minus_1) / (x_i - x_i_minus_1) + v / (x_i_plus_1 - x_i_minus_1) * (2 * x - x_i_minus_1 - x_i);
            double secondDerivative = 2 * v / (x_i_plus_1 - x_i_minus_1);

            return new double[]{firstDerivative, secondDerivative};

        }
    }

}
