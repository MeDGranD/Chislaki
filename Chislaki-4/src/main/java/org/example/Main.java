package org.example;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.example.Edge.*;
import org.example.Koshi.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        @FunctionalInterface
        interface SolverFunction {
            List<SolutionPoint> apply(double x0, RealVector y0_initial, double xEnd, double h, DifferentialEquationSystem ode);
        }

        SolverFunction[] solvers = {
                EulerSolver::solve,
                RungeKutta4Solver::solve,
                AdamsBashforth4Solver::solve
        };

        for(var solver : solvers){
            List<SolutionPoint> answer = solver.apply(
                    0,
                    new ArrayRealVector(new double[]{0.0, 1.0}),
                    1,
                    0.1,
                    new ODE()
            );
            List<SolutionPoint> answer2 = solver.apply(
                    0,
                    new ArrayRealVector(new double[]{0.0, 1.0}),
                    1,
                    0.2,
                    new ODE()
            );
            System.out.printf("%-8s | %-15s | %-15s | %-15s | %-15s\n",
                    "x", "Числ. y(x)", "Точное y(x)", "|Числ-Точн|", "Оц.Рунге-Р.");
            System.out.println("------------------------------------------------------------------------------------");

            List<Double> absErrors_h = ErrorEvaluator.absoluteError(answer);
            List<Double> rombergErrorEstimates_h = ErrorEvaluator.rungeRombergErrorEstimate(answer, answer2, 1, 0.1);

            for (int j = 0; j < answer.size(); j++) {
                SolutionPoint sp_h = answer.get(j);
                double exact_y = ExactSolution.calculate(sp_h.x());
                String rombergStr = Double.isNaN(rombergErrorEstimates_h.get(j)) ? "---" : String.format("%.7E", rombergErrorEstimates_h.get(j));

                System.out.printf("%-8.2f | %-15.7E | %-15.7E | %-15.7E | %-15s\n",
                        sp_h.x(),
                        sp_h.y()[0],
                        exact_y,
                        absErrors_h.get(j),
                        rombergStr);
            }
            System.out.println("\n");
        }

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Метод: Метод Стрельбы");
        System.out.println("------------------------------------------------------------------------------------");
        List<SolutionPoint> sol_shooting_h = ShootingMethodSolver.solve(1e-7, 100, 0.1);

        int p_shooting = 4;
        List<SolutionPoint> sol_shooting_2h_internal_step = ShootingMethodSolver.solve(1e-7, 100, 0.2);


        if (sol_shooting_h != null && !sol_shooting_h.isEmpty()) {
            List<Double> absErrors_shooting = ErrorEvaluatorBoundary.absoluteError(sol_shooting_h);
            List<Double> rombergErrors_shooting = (sol_shooting_2h_internal_step != null && !sol_shooting_2h_internal_step.isEmpty()) ?
                    ErrorEvaluatorBoundary.rungeRombergError(sol_shooting_h, sol_shooting_2h_internal_step, p_shooting) : null;
            printSolutionTable("Стрельба", sol_shooting_h, absErrors_shooting, rombergErrors_shooting);
        } else {
            System.out.println("Решение методом стрельбы не получено или пусто.");
        }
        System.out.println("\n");

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Метод: Конечно-разностный метод");
        System.out.println("------------------------------------------------------------------------------------");
        List<SolutionPoint> sol_fdm_h = FiniteDifferenceMethodSolver.solve(0.1);
        List<SolutionPoint> sol_fdm_2h = FiniteDifferenceMethodSolver.solve(2 * 0.1);
        int p_fdm = 2;

        if (sol_fdm_h != null && !sol_fdm_h.isEmpty()) {
            List<Double> absErrors_fdm = ErrorEvaluatorBoundary.absoluteError(sol_fdm_h);
            List<Double> rombergErrors_fdm = (sol_fdm_2h != null && !sol_fdm_2h.isEmpty()) ?
                    ErrorEvaluatorBoundary.rungeRombergError(sol_fdm_h, sol_fdm_2h, p_fdm) : null;
            printSolutionTable("КРМ", sol_fdm_h, absErrors_fdm, rombergErrors_fdm);
        } else {
            System.out.println("Решение конечно-разностным методом не получено или пусто.");
        }

    }

    private static void printSolutionTable(String methodName, List<SolutionPoint> solution,
                                           List<Double> absErrors, List<Double> rombergErrors) {
        if (solution == null || solution.isEmpty()) {
            System.out.println(methodName + ": No solution data to print.");
            return;
        }
        System.out.printf("%-8s | %-25s | %-15s | %-15s | %-15s\n",
                "x", methodName + " y(x)", "Точное y(x)", "|Числ-Точн|", "Оц.Рунге-Р.");
        System.out.println("-----------------------------------------------------------------------------------------------------");

        for (int j = 0; j < solution.size(); j++) {
            SolutionPoint sp_h = solution.get(j);
            double exact_y = ExactSolutionBoundary.calculate(sp_h.x());
            String rombergStr = (rombergErrors != null && j < rombergErrors.size() && !Double.isNaN(rombergErrors.get(j)))
                    ? String.format("%.7E", rombergErrors.get(j))
                    : "---";
            String absErrorStr = (absErrors != null && j < absErrors.size() && !Double.isNaN(absErrors.get(j)))
                    ? String.format("%.7E", absErrors.get(j))
                    : "---";

            System.out.printf("%-8.2f | %-25.7E | %-15.7E | %-15s | %-15s\n",
                    sp_h.x(),
                    sp_h.y()[0],
                    exact_y,
                    absErrorStr,
                    rombergStr);
        }
    }

}