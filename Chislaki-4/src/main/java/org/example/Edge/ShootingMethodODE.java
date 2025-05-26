package org.example.Edge;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.example.DifferentialEquationSystem;

import java.util.Arrays;

public class ShootingMethodODE implements DifferentialEquationSystem {
    public int getNumEquations() {
        return 2; // z1 = y, z2 = y'
    }

    public RealVector getDerivatives(double x, RealVector z) {
        // z[0] = y (z1)
        // z[1] = y' (z2)
        RealVector zDot = new ArrayRealVector(getNumEquations());
        zDot.setEntry(0, z.getEntry(1)); // z1' = z2
        if (Math.abs(x) < 1e-12) {
            System.err.println("Warning: x is close to zero in ShootingMethodODEImpl derivatives calculation: " + x);
            return zDot;
        }
        zDot.setEntry(1, ((2 * x + 1) / x) * z.getEntry(1) - ((2 * x + 1) / x) * z.getEntry(0));
        return zDot;
    }

}
