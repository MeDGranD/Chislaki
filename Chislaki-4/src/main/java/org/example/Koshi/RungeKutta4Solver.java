package org.example.Koshi;

import org.apache.commons.math3.linear.RealVector;
import org.example.DifferentialEquationSystem;
import org.example.SolutionPoint;

import java.util.ArrayList;
import java.util.List;

public class RungeKutta4Solver implements Solver {
    static public List<SolutionPoint> solve(double x0, RealVector y0_initial, double xEnd, double h, DifferentialEquationSystem ode) {
        List<SolutionPoint> solution = new ArrayList<>();
        double x = x0;
        RealVector y = y0_initial.copy();

        solution.add(new SolutionPoint(x, y.toArray()));

        int numSteps = (int) Math.round((xEnd - x0) / h);

        for (int i = 0; i < numSteps; i++) {
            if (x >= xEnd) break;

            RealVector k1 = ode.getDerivatives(x, y).mapMultiply(h);

            RealVector y_temp_k2 = y.add(k1.mapDivide(2.0));
            RealVector k2 = ode.getDerivatives(x + h/2.0, y_temp_k2).mapMultiply(h);

            RealVector y_temp_k3 = y.add(k2.mapDivide(2.0));
            RealVector k3 = ode.getDerivatives(x + h/2.0, y_temp_k3).mapMultiply(h);

            RealVector y_temp_k4 = y.add(k3.mapDivide(2.0));
            RealVector k4 = ode.getDerivatives(x + h, y_temp_k4).mapMultiply(h);

            RealVector y_next = y.add(k1.add(k2.mapMultiply(2)).add(k3.mapMultiply(2)).add(k4).mapDivide(6.0));

            x += h;
            y = y_next;
            solution.add(new SolutionPoint(x, y.toArray()));
        }
        return solution;
    }
}
