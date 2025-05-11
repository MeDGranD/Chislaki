package org.example;

import org.example.Integration.*;
import org.example.Interpolation.CubicSplineInterpolator;
import org.example.Interpolation.InterpolationPoint;
import org.example.Interpolation.LagrangeInterpolator;
import org.example.Interpolation.NewtonInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {

    private static final Function<Double, Double> func = Math::atan;

    public static void main(String[] args) {

        double[] X = {-3, -1, 1, 3};
        List<InterpolationPoint> points = new ArrayList<>();

        for(double x : X){
            points.add(new InterpolationPoint(x, func.apply(x)));
        }

        LagrangeInterpolator LInterpolator = new LagrangeInterpolator(points);
        NewtonInterpolator NInterpolator = new NewtonInterpolator(points);

        double LPoint = LInterpolator.interpolate(-0.5);
        double NPoint = NInterpolator.interpolate(-0.5);
        double TruePoint = func.apply(-0.5);

        System.out.println(TruePoint);
        System.out.println(LPoint);
        System.out.println(Math.abs(LPoint-TruePoint));
        System.out.println(NPoint);
        System.out.println(Math.abs(NPoint-TruePoint));

        System.out.println();

        double[] cX = {-3, -1, 1, 3, 5};
        double[] cY = {-1.249, -0.7854, 0.7854, 1.249, 1.3734};
        //double[] cX = {-3, -1, 1, 3, 5};
        //double[] cY = {2.8198, 2.3562, 0.78540, 0.32175, 0.19740};
        List<InterpolationPoint> cPoints = new ArrayList<>();
        int i = 0;
        for(double x : cX){
            cPoints.add(new InterpolationPoint(x, cY[i++]));
        }

        CubicSplineInterpolator cubicSplineInterpolator = new CubicSplineInterpolator(cPoints);

        System.out.println(cubicSplineInterpolator.interpolate(-0.5));
        System.out.println();

        double[] X3 = {-5, -3, -1, 1, 3, 5};
        double[] Y3 = {-1.3734, -1.249, -0.7854, 0.7854, 1.249, 1.3734};
        List<InterpolationPoint> points3 = new ArrayList<>();
        int i3 = 0;
        for(double x : X3){
            points3.add(new InterpolationPoint(x, Y3[i3++]));
        }
        LeastSquaresApproximator approximator2 = new LeastSquaresApproximator(points3, 2);
        LeastSquaresApproximator approximator3 = new LeastSquaresApproximator(points3, 3);

        System.out.println(Arrays.toString(approximator2.getCoefs()));
        System.out.println(approximator2.squareError());
        System.out.println(approximator2.evaluate(1));
        System.out.println(Arrays.toString(approximator3.getCoefs()));
        System.out.println(approximator3.squareError());
        System.out.println(approximator3.evaluate(1));

        System.out.println();

        double[] X4 = {0, 0.5, 1, 1.5, 2};
        double[] Y4 = {0, 0.97943, 1.8415, 2.4975, 2.9093};
        List<InterpolationPoint> points4 = new ArrayList<>();
        int i4 = 0;
        for(double x : X4){
            points4.add(new InterpolationPoint(x, Y4[i4++]));
        }

        NumericDifferentiator differentiator = new NumericDifferentiator(points4);
        System.out.println(Arrays.toString(differentiator.calculateCentralDerivativesAtNode(1)));

        System.out.println();

        NumericalIntegrator rectInt = new RectangleIntegrator();
        NumericalIntegrator trapInt = new TrapezoidIntegrator();
        NumericalIntegrator simpInt = new SimpsonIntegrator();

        Function<Double, Double> func = (x) -> Math.pow(x, 2) / (Math.pow(x, 2) + 16);

        System.out.println(rectInt.integrate(func, 0, 2, 0.5));
        System.out.println(rectInt.integrate(func, 0, 2, 0.25));
        System.out.println(ErrorEstimator.estimateRungeRombergError(
                rectInt.integrate(func, 0, 2, 0.5),
                rectInt.integrate(func, 0, 2, 0.25),
                2,
                1
        ));

        System.out.println();

        System.out.println(trapInt.integrate(func, 0, 2, 0.5));
        System.out.println(trapInt.integrate(func, 0, 2, 0.25));
        System.out.println(ErrorEstimator.estimateRungeRombergError(
                trapInt.integrate(func, 0, 2, 0.5),
                trapInt.integrate(func, 0, 2, 0.25),
                2,
                2
        ));

        System.out.println();

        System.out.println(simpInt.integrate(func, 0, 2, 0.5));
        System.out.println(simpInt.integrate(func, 0, 2, 0.25));
        System.out.println(ErrorEstimator.estimateRungeRombergError(
                simpInt.integrate(func, 0, 2, 0.5),
                simpInt.integrate(func, 0, 2, 0.25),
                2,
                4
        ));

    }
}