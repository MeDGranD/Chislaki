package org.example.Koshi;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.example.DifferentialEquationSystem;

public class ODE implements DifferentialEquationSystem {
    public int getNumEquations() {
        return 2;
    }

    public RealVector getDerivatives(double x, RealVector y) {
        RealVector derivatives = new ArrayRealVector(getNumEquations());
        derivatives.setEntry(0, y.getEntry(1)); // z1' = z2
        derivatives.setEntry(1, -y.getEntry(1)*Math.tan(x)-y.getEntry(0)*Math.pow(Math.cos(x), 2)); // z2' = -z2*tan(x) - z1*cos(x)^2
        return derivatives;
    }
}
