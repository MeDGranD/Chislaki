package org.example.Koshi;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.example.DifferentialEquationSystem;
import org.example.SolutionPoint;

import java.util.ArrayList;
import java.util.List;

public class AdamsBashforth4Solver implements Solver {
    static public List<SolutionPoint> solve(double x0, RealVector y0_initial, double xEnd, double h, DifferentialEquationSystem ode) {
        List<SolutionPoint> solution = new ArrayList<>();
        double x = x0;
        RealVector y = y0_initial.copy();
        int numEquations = ode.getNumEquations();

        solution.add(new SolutionPoint(x, y.toArray()));

        List<SolutionPoint> initial_points = RungeKutta4Solver.solve(x0, y0_initial, x0 + 3*h, h, ode);

        if (initial_points.size() < 4) {
            System.err.println("Adams method needs at least 4 starting points. Not enough points from RK4.");
            return initial_points.isEmpty() ? solution : initial_points;
        }

        for (int i = 1; i < initial_points.size() && i <= 3; i++) {
            solution.add(initial_points.get(i));
        }
        double[][] f_history = new double[4][numEquations];

        for (int i=0; i<4; i++) {
            SolutionPoint sp = solution.get(i);
            f_history[i] = ode.getDerivatives(sp.x(), new ArrayRealVector(sp.y())).toArray();
        }

        x = solution.get(3).x();
        y = new ArrayRealVector(solution.get(3).y());

        int numSteps = (int) Math.round((xEnd - x0) / h);

        for (int k = 3; k < numSteps; k++) {
            if (x >= xEnd - h/2.0) break;

            double[] f_k   = f_history[3];
            double[] f_km1 = f_history[2];
            double[] f_km2 = f_history[1];
            double[] f_km3 = f_history[0];

            RealVector y_next = new ArrayRealVector(numEquations);
            for (int j = 0; j < numEquations; j++) {
                y_next.setEntry(j, y.getEntry(j) + (h/24)*(55 * f_k[j] - 59 * f_km1[j] + 37 * f_km2[j] - 9 * f_km3[j]));
            }

            x += h;
            y = y_next;
            solution.add(new SolutionPoint(x, y.toArray()));

            System.arraycopy(f_history, 1, f_history, 0, 3);
            f_history[3] = ode.getDerivatives(x, y).toArray();
        }
        return solution;
    }
}
