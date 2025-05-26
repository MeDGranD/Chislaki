package org.example;

import org.apache.commons.math3.linear.RealVector;

public interface DifferentialEquationSystem {
    int getNumEquations();
    RealVector getDerivatives(double x, RealVector z);
}
