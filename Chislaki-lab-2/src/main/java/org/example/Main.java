package org.example;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        // --- Часть 1: Одиночное уравнение (можно закомментировать) ---
        System.out.println("--- Решение одиночного нелинейного уравнения ---");
        solveSingleEquation();
        System.out.println("\n==================================================\n");

        // --- Часть 2: Система уравнений ---
        System.out.println("--- Решение системы нелинейных уравнений ---");
        solveSystemOfEquations();
    }

    // --- Метод для Части 1 ---
    public static void solveSingleEquation() {

        Function<Double, Double> func = (x) -> Math.sqrt((Math.sin(x) + 0.5) / 2);

        double xVal = SimpleIterationSolver.getRoot(
                func,
                0,
                1,
                0.5,
                1e-8,
                1000
        );

        System.out.println(xVal);

        Function<Double, Double> funcN = (x) -> Math.sin(x) - 2 * x * x + 0.5;

        xVal = NewtonSolver.getRoot(
                funcN,
                1,
                1e-8,
            1000
        );

        System.out.println(xVal);
    }

    // --- Метод для Части 2 ---
    public static void solveSystemOfEquations() {

        Function<double[], Double> func1 = (x) -> x[0] - Math.cos(x[1]) - 1;
        Function<double[], Double> func2 = (x) -> x[1] - Math.sin(x[0]) - 1;

        @SuppressWarnings("unchecked")
        double[] ans = SystemNewtonSolver.getRoots(
                new double[]{0d, 0d},
                new Function[]{func1, func2},
                1e-8,
                1000
        );

        System.out.println(Arrays.toString(
                ans
        ));
        System.out.println(
                func1.apply(ans)
        );
        System.out.println(
                func2.apply(ans)
        );

        Function<double[], Double> func1S = (x) -> Math.cos(x[1]) + 1;
        Function<double[], Double> func2S = (x) -> Math.sin(x[0]) + 1;

        @SuppressWarnings("unchecked")
        double[] ansS = SystemSimpleIterationSolver.getRoots(
                new Function[]{func1S, func2S},
                new double[]{0d, 0d},
                1e-8,
                1000
        );

        System.out.println(Arrays.toString(
                ansS
        ));
        System.out.println(
                func1.apply(ansS)
        );
        System.out.println(
                func2.apply(ansS)
        );

    }
}