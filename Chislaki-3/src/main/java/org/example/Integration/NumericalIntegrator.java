package org.example.Integration;

import java.util.function.Function;

@FunctionalInterface
public interface NumericalIntegrator {

    double integrate(Function<Double, Double> func, double a, double b, double h);

}
