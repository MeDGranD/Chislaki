package org.example.Edge;

import org.example.SolutionPoint;

import java.util.ArrayList;
import java.util.List;

public class ErrorEvaluatorBoundary {
    public static List<Double> absoluteError(List<SolutionPoint> numericalSolution) {
        if (numericalSolution == null) return null;
        List<Double> errors = new ArrayList<>();
        for (SolutionPoint sp : numericalSolution) {
            double y_exact_val = ExactSolutionBoundary.calculate(sp.x());
            errors.add(Math.abs(sp.y()[0] - y_exact_val));
        }
        return errors;
    }

    public static List<Double> rungeRombergError(List<SolutionPoint> sol_h, List<SolutionPoint> sol_2h, int p) {
        if (sol_h == null || sol_2h == null) return null;
        List<Double> rombergErrors = new ArrayList<>();
        int idx_2h = 0;
        for (int idx_h = 0; idx_h < sol_h.size(); idx_h++) {
            SolutionPoint p_h = sol_h.get(idx_h);
            if (idx_2h < sol_2h.size() && Math.abs(p_h.x() - sol_2h.get(idx_2h).x()) < 1e-9 ) {
                SolutionPoint p_2h_current = sol_2h.get(idx_2h);
                double error_estimate = Math.abs(p_h.y()[0] - p_2h_current.y()[0]) / (Math.pow(2, p) - 1.0);
                rombergErrors.add(error_estimate);
                idx_2h++;
            } else {
                rombergErrors.add(Double.NaN);
            }
        }
        while(rombergErrors.size() < sol_h.size()){
            rombergErrors.add(Double.NaN);
        }
        return rombergErrors;
    }
}
