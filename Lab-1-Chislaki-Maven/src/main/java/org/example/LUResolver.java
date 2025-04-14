package org.example;

import org.apache.commons.math3.linear.*;

import java.util.Arrays;

public class LUResolver {

    private final RealMatrix data; //N*M
    private final RealMatrix U;
    private final RealMatrix L;
    private final RealMatrix P;
    private final int N, M;

    public LUResolver(double[][] data){

        if(data.length == 0 || data[0] == null || data.length != data[0].length)
            throw new IllegalArgumentException("Неверные размеры матрицы");

        this.data = new Array2DRowRealMatrix(data, true);
        U = this.data.copy();
        L = MatrixUtils.createRealIdentityMatrix(data.length);
        P = L.copy();
        N = data.length;
        M = data[0].length;
        luDecomposition();
    }

    public RealMatrix[] getLU(){
        return new RealMatrix[]{L, U};
    }

    public RealMatrix getData(){
        return data;
    }

    private void swapRows(RealMatrix matrix, int row1, int row2) {
        double[] temp = matrix.getRow(row1);
        matrix.setRow(row1, matrix.getRow(row2));
        matrix.setRow(row2, temp);
    }

    private void luDecomposition() {

        //Обходим столбцы матрицы A
        for(int i = 0; i < M; ++i){

            //Находим максимальный элемент в стоблце
            int pivot = 0;
            double max = 0;
            for(int j = i; j < N; ++j){
                if(Math.abs(U.getEntry(j, i)) > max){
                    max = Math.abs(U.getEntry(j, i));
                    pivot = j;
                }
            }
            //Если максимальный элемент 0 (т.е. все 0) - то невозможно разложить
            if(max == 0){
                throw new RuntimeException("Невозможно найти LU");
            }
            if (pivot != i) {
                swapRows(U, i, pivot);
                swapRows(L, i, pivot);
                swapRows(P, i, pivot);
            }

            //Меняем местами в матрице L
            for(int j = 0; j < N; ++j){
                double temp = L.getEntry(j, i);
                L.setEntry(j, i, L.getEntry(j, pivot));
                L.setEntry(j, pivot, temp);
            }

            //Зануляем элементы под главным элементом
            for(int j = i + 1; j < N; ++j){

                L.setEntry(j, i,
                        U.getEntry(j, i) / U.getEntry(i, i)
                );

                for(int k = i; k < M; ++k){
                    U.addToEntry(j, k, -1 * U.getEntry(i, k) * L.getEntry(j, i));
                }

            }

        }

    }

    public RealVector solveLU(double[] bData) {

        if (bData.length != N) {
            throw new IllegalArgumentException("Неверные размеры вектора");
        }

        //Превращаем входной массив в вектор и перемножаем на P => и возвращаем массив с измененными строками
        RealVector b = P.operate(new ArrayRealVector(bData, true));

        //Прямой ход (вычисляем z)
        double[] z = new double[N];
        for (int i = 0; i < N; i++) {
            z[i] = b.getEntry(i);
            for (int j = 0; j < i; j++) {
                z[i] -= L.getEntry(i, j) * z[j];
            }
        }

        //Обратный ход (вычисляем x)
        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            x[i] = z[i];
            for (int j = i + 1; j < N; j++) {
                x[i] -= U.getEntry(i, j) * x[j];
            }
            x[i] /= U.getEntry(i, i);
        }

        return new ArrayRealVector(x, true);

    }

    public double determinant(){

        double result = 1;
        int swaps = 0;
        // Считаем перестановки в P (например, сравнивая с единичной матрицей)
        for (int i = 0; i < P.getRowDimension(); i++) {
            if (P.getEntry(i, i) != 1) swaps++;
        }
        for (int i = 0; i < M; ++i) {
            result *= U.getEntry(i, i);
        }
        return (swaps % 2 == 0) ? result : -result;
    }

    public RealMatrix inverse(){

        RealMatrix answer = new Array2DRowRealMatrix(N, M);
        RealMatrix E = MatrixUtils.createRealIdentityMatrix(N);

        for(int i = 0; i < N; ++i)
            answer.setColumn(i,
                    solveLU(E.getRow(i)).toArray()
            );

        return answer;
    }



}
