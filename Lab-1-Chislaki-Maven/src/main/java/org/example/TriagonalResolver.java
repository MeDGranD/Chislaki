package org.example;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class TriagonalResolver {

    private static double null_exp = 1e-12;

    public static RealVector solveTridiagonal(RealMatrix mat, double[] d) {

        if(mat.getRowDimension() == 0 || mat.getColumnDimension() == 0 || mat.getColumnDimension() != mat.getRowDimension())
            throw new IllegalArgumentException("Неверные размеры матрицы");

        int n = mat.getRowDimension();
        double[] P = new double[n];
        double[] Q = new double[n];
        double[] x = new double[n];

        //Окно для обработки матрицы
        int a = -1,b = 0,c = 1;

        if (Math.abs(mat.getEntry(0, 0)) < null_exp) {  // Достаточное условие для первого элемента
            throw new RuntimeException("Перый диагональный элемент слишком мал или равен нулю");
        }

        //Вычисления P и Q для каждой строки
        P[0] = -mat.getEntry(0, c) / mat.getEntry(0, b);
        Q[0] = d[0] / mat.getEntry(0, b);

        for (int i = 1; i < n - 1; i++) {
            ++a; ++b; ++c;
            double denom = mat.getEntry(i, b) + mat.getEntry(i, a) * P[i - 1];
            if (Math.abs(denom) < null_exp)  // Проверка знаменателя на вырожденность
                throw new RuntimeException("Система вырождена или требует особого подхода на шаге " + i);
            P[i] = -mat.getEntry(i, c) / denom;
            Q[i] = (d[i] - mat.getEntry(i, a) * Q[i - 1]) / denom;
        }

        int last = n-1;
        double a_last = mat.getEntry(last, last-1);
        double b_last = mat.getEntry(last, last);
        double denom_last = b_last + a_last * P[last-1];

        if (Math.abs(denom_last) < null_exp) {  // Доп проверка знаменателя для последней строки
            throw new RuntimeException("Система вырождена в последней строке");
        }

        Q[last] = (d[last] - a_last * Q[last-1]) / denom_last;
        x[last] = Q[last];

        //Вычисление x
        for (int i = n - 2; i >= 0; i--) {
            x[i] = P[i] * x[i + 1] + Q[i];
        }

        return new ArrayRealVector(x, true);
    }

}
