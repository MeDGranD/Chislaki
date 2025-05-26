package org.example.Edge;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.example.Koshi.RungeKutta4Solver;
import org.example.SolutionPoint;

import java.util.ArrayList;
import java.util.List;

public class ShootingMethodSolver {
    private static final double x_start = 1.0;
    private static final double x_end = 2.0;
    private static final double y_prime_at_x_start = 3 * Math.E;

    private static double evaluateBoundaryConditionAtXEnd(double eta_y_at_x_start, double h_ode_solver) {
        RealVector z_initial = new ArrayRealVector(new double[]{eta_y_at_x_start, y_prime_at_x_start});

        List<SolutionPoint> ode_solution = RungeKutta4Solver.solve(x_start, z_initial, x_end, h_ode_solver, new ShootingMethodODE());

        if (ode_solution.isEmpty()) {
            System.err.println("Shooting method: ODE solver returned no solution for eta=" + eta_y_at_x_start);
            return Double.NaN;
        }

        SolutionPoint finalPoint = ode_solution.getLast();
        if (Math.abs(finalPoint.x() - x_end) > h_ode_solver / 2.0) {
            System.err.printf("Shooting method: ODE solver did not reach x_end. Reached %.4f for eta=%.4f\n", finalPoint.x(), eta_y_at_x_start);
            return Double.NaN;
        }

        double y_at_x_end = finalPoint.y()[0];
        double y_prime_at_x_end = finalPoint.y()[1];

        return y_prime_at_x_end - 2 * y_at_x_end;
    }

    private static double secantMethod(FunctionToSolve phiFunc, double x0, double x1, double tolerance, int maxIterations, double h_ode_solver) {
        double fx0 = phiFunc.evaluate(x0, h_ode_solver);
        double fx1 = phiFunc.evaluate(x1, h_ode_solver);

        if (Double.isNaN(fx0) || Double.isNaN(fx1)) {
            System.err.println("Secant method: Function evaluation returned NaN at initial guesses.");
            return Double.NaN;
        }

        for (int i = 0; i < maxIterations; i++) {
            if (Math.abs(fx1) < tolerance) {
                return x1;
            }
            if (Math.abs(fx1 - fx0) < 1e-12) {
                System.err.println("Secant method: Denominator too small (fx1 - fx0 is near zero).");
                return Double.NaN;
            }

            double x2 = x1 - fx1 * (x1 - x0) / (fx1 - fx0);
            x0 = x1;
            fx0 = fx1;
            x1 = x2;
            fx1 = phiFunc.evaluate(x1,h_ode_solver);

            if (Double.isNaN(fx1)) {
                System.err.println("Secant method: Function evaluation returned NaN at iteration " + i);
                return Double.NaN;
            }
        }
        System.err.println("Secant method: Maximum iterations reached without convergence.");
        return x1;
    }

    @FunctionalInterface
    interface FunctionToSolve{
        double evaluate(double x, double h_ode_solver);
    }

    public static List<SolutionPoint> solve(double eta_tolerance, int max_eta_iterations, double h_ode_solver) {
        double eta0 = ExactSolutionBoundary.calculate(x_start) - 0.5;
        double eta1 = ExactSolutionBoundary.calculate(x_start) + 0.5;
        if (Math.abs(eta0 - eta1) < 1e-9) eta1 = eta0 + 0.1;

        double optimal_eta = secantMethod(ShootingMethodSolver::evaluateBoundaryConditionAtXEnd, eta0, eta1, eta_tolerance, max_eta_iterations, h_ode_solver);

        if (Double.isNaN(optimal_eta)) {
            System.err.println("Shooting method: Failed to find optimal eta.");
            eta0 = 1.0; eta1 = 5.0;
            System.err.println("Shooting method: Retrying eta search with eta0=" + eta0 + ", eta1=" + eta1);
            optimal_eta = secantMethod(ShootingMethodSolver::evaluateBoundaryConditionAtXEnd, eta0, eta1, eta_tolerance, max_eta_iterations, h_ode_solver);
            if (Double.isNaN(optimal_eta)) {
                System.err.println("Shooting method: Failed to find optimal eta on retry.");
                return null;
            }
        }

        System.out.printf("Shooting method: Optimal eta (y(%.1f)) found: %.7E\n", x_start, optimal_eta);

        RealVector z_initial_optimal = new ArrayRealVector(new double[]{optimal_eta, y_prime_at_x_start});
        List<SolutionPoint> ode_solution_optimal = RungeKutta4Solver.solve(x_start, z_initial_optimal, x_end, h_ode_solver, new ShootingMethodODE());

        List<SolutionPoint> finalSolution = new ArrayList<>();
        for (SolutionPoint sp : ode_solution_optimal) {
            finalSolution.add(new SolutionPoint(sp.x(), sp.y()));
        }
        return finalSolution;
    }
}
