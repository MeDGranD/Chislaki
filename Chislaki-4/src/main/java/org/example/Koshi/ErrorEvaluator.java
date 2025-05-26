package org.example.Koshi;

import org.example.SolutionPoint;

import java.util.ArrayList;
import java.util.List;

public class ErrorEvaluator {
    public static List<Double> absoluteError(List<SolutionPoint> numericalSolution) {
        List<Double> errors = new ArrayList<>();
        for (SolutionPoint sp : numericalSolution) {
            double y_exact_val = ExactSolution.calculate(sp.x());
            errors.add(Math.abs(sp.y()[0] - y_exact_val));
        }
        return errors;
    }

    public static List<Double> rungeRombergErrorEstimate(List<SolutionPoint> sol_h, List<SolutionPoint> sol_2h, int p, double h_step) {
        List<Double> rombergEstimatesForSol_h_grid = new ArrayList<>();
        int sol2h_idx = 0;

        for (SolutionPoint current_sol_h_point : sol_h) {
            double current_x = current_sol_h_point.x();

            boolean foundMatchIn2h = false;
            if (sol2h_idx < sol_2h.size()) {
                SolutionPoint current_sol_2h_point = sol_2h.get(sol2h_idx);
                if (Math.abs(current_x - current_sol_2h_point.x()) < h_step / 2.0) {
                    double y_h_val = current_sol_h_point.y()[0];
                    double y_2h_val = current_sol_2h_point.y()[0];
                    double error_estimate = Math.abs(y_h_val - y_2h_val) / (Math.pow(2, p) - 1.0);
                    rombergEstimatesForSol_h_grid.add(error_estimate);
                    sol2h_idx++;
                    foundMatchIn2h = true;
                } else if (current_sol_2h_point.x() < current_x) {
                    sol2h_idx++;
                }
            }

            if (!foundMatchIn2h) {
                rombergEstimatesForSol_h_grid.add(Double.NaN);
            }
        }
        return rombergEstimatesForSol_h_grid;
    }
}
