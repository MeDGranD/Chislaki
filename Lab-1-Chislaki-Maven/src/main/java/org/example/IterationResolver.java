package org.example;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;

public class IterationResolver {

    public static RealVector solveSimpleIterations(RealMatrix mat, double[] b, double tol, int maxIter) {

        if(mat.getRowDimension() == 0 || mat.getColumnDimension() == 0 || mat.getColumnDimension() != mat.getRowDimension())
            throw new IllegalArgumentException("Неверные размеры матрицы");

        int rows = mat.getRowDimension();
        int cols = mat.getColumnDimension();

        for(int i = 0; i < rows; ++i){
            for(int j = 0; j < cols; ++j){
                if(Math.abs(mat.getEntry(i, j))>Math.abs(mat.getEntry(i, i)))
                    throw new RuntimeException("Отсутсвуют максимумы на диагонале");
            }
        }

        double[] B = new double[rows];
        double[][] A = new double[rows][cols];

        //Заполение коэффициентов системы
        for(int i = 0; i < rows; ++i){
            B[i] = b[i] / mat.getEntry(i, i);
            for(int j = 0; j < cols; ++j){
                A[i][j] = j == i ? 0 : -mat.getEntry(i, j) / mat.getEntry(i, i);
            }
        }

        //Нахождение альфы для вычисления значения точности итерации
        double a = 0;
        for(int j = 0; j  < cols; ++j){
            double sum = 0;
            for(int i = 0; i < rows; ++i){
                sum += Math.abs(A[i][j]);
            }
            a = Math.max(a, sum);
        }

        //Начальные x
        double[] x = Arrays.copyOf(B, B.length);

        //Итерации с ограничением
        int iter = 0;
        for(; iter < maxIter; ++iter){

            double[] newX = new double[rows];
            for(int i = 0; i < rows; ++i){
                double sum = 0;
                for(int j = 0; j < cols; ++j){
                    sum += A[i][j] * x[j];
                }
                newX[i] = sum + B[i];
            }

            //Вычсиление ошибки (т.е. точности)
            double error = 0;
            for(int i = 0; i < rows; ++i){
                error = Math.max(error, Math.abs((a/(1 - a))*(newX[i] - x[i])));
            }

            x = newX;

            if(error <= tol) {
                break;
            }

        }
        System.out.println("Корни нашлись за " + iter + " итераций! (Простые итерации)");
        return new ArrayRealVector(x, true);

    }

    public static RealVector solveSeidel(RealMatrix mat, double[] b, double tol, int maxIter) {

        if(mat.getRowDimension() == 0 || mat.getColumnDimension() == 0 || mat.getColumnDimension() != mat.getRowDimension())
            throw new IllegalArgumentException("Неверные размеры матрицы");

        int rows = mat.getRowDimension();
        int cols = mat.getColumnDimension();

        for(int i = 0; i < rows; ++i){
            for(int j = 0; j < cols; ++j){
                if(Math.abs(mat.getEntry(i, j))>Math.abs(mat.getEntry(i, i)))
                    throw new RuntimeException("Отсутсвуют максимуми на диагонале");
            }
        }

        double[] B = new double[rows];
        double[][] A = new double[rows][cols];
        for(int i = 0; i < rows; ++i){
            B[i] = b[i] / mat.getEntry(i, i);
            for(int j = 0; j < cols; ++j){
                A[i][j] = j == i ? 0 : -mat.getEntry(i, j) / mat.getEntry(i, i);
            }
        }

        double a = 0;
        for(int j = 0; j  < cols; ++j){
            double sum = 0;
            for(int i = 0; i < rows; ++i){
                sum += Math.abs(A[i][j]);
            }
            a = Math.max(a, sum);
        }

        double[] x = Arrays.copyOf(B, B.length);
        int iter = 0;
        for(; iter < maxIter; ++iter){

            double[] newX = new double[rows];
            for(int i = 0; i < rows; ++i){
                double sum = 0;
                for(int j = 0; j < cols; ++j){
                    sum += j < i ? A[i][j]*newX[j] : A[i][j] * x[j];
                }
                newX[i] = sum + B[i];
            }

            double error = 0;
            for(int i = 0; i < rows; ++i){
                error = Math.max(error, Math.abs((a/(1 - a))*(newX[i] - x[i])));
            }

            x = newX;

            if(error <= tol) {
                break;
            }

        }
        System.out.println("Корни нашлись за " + iter + " итераций! (Зейдель)");
        return new ArrayRealVector(x, true);

    }

}
