package org.example.Koshi;

import org.apache.commons.math3.linear.RealVector;
import org.example.DifferentialEquationSystem;
import org.example.SolutionPoint;

import java.util.ArrayList;
import java.util.List;

public class EulerSolver implements Solver {
    static public List<SolutionPoint> solve(double x0, RealVector y0_initial, double xEnd, double h, DifferentialEquationSystem ode) {
        List<SolutionPoint> solution = new ArrayList<>();
        double x = x0;
        RealVector y = y0_initial.copy();

        solution.add(new SolutionPoint(x, y.toArray()));

        int numSteps = (int) Math.round((xEnd - x0) / h);

        for (int i = 0; i < numSteps; i++) {
            if (x >= xEnd) break;

            RealVector derivatives = ode.getDerivatives(x, y);
            RealVector y_next = y.copy();

            for (int j = 0; j < y.getDimension(); j++) {
                y_next = y_next.add(derivatives.mapMultiply(h));
            }

            x += h;
            y = y_next;
            solution.add(new SolutionPoint(x, y.toArray()));
        }
        return solution;
    }
}
