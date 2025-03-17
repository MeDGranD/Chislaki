package org.example;

import java.util.Arrays;

public final class Matrix {
    private final Double[][] data;
    private Double[][] L = null;
    private Double[][] U = null;
    private Double[][] P = null;
    private final int rows;
    private final int cols;

    public Matrix(Double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new Double[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("Все строки должны иметь одинаковую длину");
            }
            this.data[i] = Arrays.copyOf(data[i], cols);
        }
    }

    public int[] getSize() {
        return new int[]{rows, cols};
    }

    public double get(int row, int col){
        if(row < 0 || row >= this.rows || col < 0 || col >= this.cols)
            throw new IllegalArgumentException("Индексы доступа для матрицы выходят за пределы ее размера");
        return data[row][col];
    }

    public Double[][] getData() {
        return Arrays.stream(data)
                .map(arr -> Arrays.copyOf(arr, cols))
                .toArray(Double[][]::new);
    }

    public Matrix transpose() {
        Double[][] transposed = new Double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = data[i][j];
            }
        }
        return new Matrix(transposed);
    }

    public Matrix multiply(double scalar) {
        return new Matrix(
                (Double [][]) Arrays.stream(data)
                        .map(arr -> (Double[]) Arrays.stream(arr).map(num -> num*scalar).toArray())
                        .toArray(Double[][]::new)
        );
    }

    public Matrix multiply(Matrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Число столбцов первой матрицы должно быть равно числу строк второй матрицы");
        }
        Double[][] result = new Double[this.rows][other.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                result[i][j] = 0d;
                for (int k = 0; k < this.cols; k++) {
                    result[i][j] += this.data[i][k] * other.data[k][j];
                }
            }
        }
        return new Matrix(result);
    }

    public Matrix add(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Матрицы должны быть одинакового размера");
        }
        Double[][] result = new Double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }
        return new Matrix(result);
    }

    public static Matrix genE(int n){
        Double[][] data = new Double[n][n];
        for(int i = 0; i < n; ++i){
            for(int j = 0; j < n; ++j){
                data[i][j] = i == j ? 1d : 0d;
            }
        }
        return new Matrix(data);
    }

    public static Matrix[] luDecomposition(Matrix mat) {

        if(mat.L != null && mat.U != null && mat.P != null){
            return new Matrix[]{
                    new Matrix(mat.L),
                    new Matrix(mat.U)};
        }

        mat.U = mat.getData();
        mat.L = genE(mat.cols).getData();
        mat.P = genE(mat.cols).getData();

        //Обходим столбцы матрицы A
        for(int i = 0; i < mat.cols; ++i){

            //Находим максимальный элемент в стоблце
            int pivot = 0;
            double max = 0;
            for(int j = i; j < mat.rows; ++j){
                if(Math.abs(mat.U[j][i]) > max){
                    max = Math.abs(mat.U[j][i]);
                    pivot = j;
                }
            }
            //Если максимальный элемент 0 (т.е. все 0) - то невозможно разложить
            if(max == 0){
                throw new RuntimeException("Невозможно найти LU");
            }
            {
                //Меняем местами матрице А
                Double[] temp = mat.U[i];
                mat.U[i] = mat.U[pivot];
                mat.U[pivot] = temp;

                //Меняем местами в матрице L
                temp = mat.L[i];
                mat.L[i] = mat.L[pivot];
                mat.L[pivot] = temp;

                //Отражаем перенос строк в матрице P
                temp = mat.P[i];
                mat.P[i] = mat.P[pivot];
                mat.P[pivot] = temp;
            }

            //Меняем местами в матрице L
            for(int j = 0; j < mat.rows; ++j){
                Double temp = mat.L[j][i];
                mat.L[j][i] = mat.L[j][pivot];
                mat.L[j][pivot] = temp;
            }

            //Зануляем элементы под главным элементом
            for(int j = i + 1; j < mat.rows; ++j){
                mat.L[j][i] = mat.U[j][i] / mat.U[i][i];
                for(int k = i; k < mat.cols; ++k){
                    mat.U[j][k] -= mat.U[i][k] * mat.L[j][i];
                }
            }

        }

        return new Matrix[]{new Matrix(mat.L), new Matrix(mat.U)};

    }

    public static Double[] solveLU(Matrix mat, Double[] b) {

        if (mat.rows != mat.cols || b.length != mat.rows) {
            throw new IllegalArgumentException("Неверные размеры матрицы или вектора");
        }
        if(mat.L == null || mat.U == null || mat.P == null)
            luDecomposition(mat);

        //Превращаем входной массив в матрицу и перемножаем на P => и возвращаем массив с измененными строками
        b = Arrays.stream(new Matrix(mat.P).multiply(
                        new Matrix(new Double[][]{b}).transpose()
                ).getData())
                .map(arr -> arr[0])
                .toArray(Double[]::new);

        //Прямой ход (вычисляем z)
        int n = mat.rows;
        double[] z = new double[n];
        for (int i = 0; i < n; i++) {
            z[i] = b[i];
            for (int j = 0; j < i; j++) {
                z[i] -= mat.L[i][j] * z[j];
            }
        }

        //Обратный ход (вычисляем x)
        Double[] x = new Double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = z[i];
            for (int j = i + 1; j < n; j++) {
                x[i] -= mat.U[i][j] * x[j];
            }
            x[i] /= mat.U[i][i];
        }

        return x;

    }

    //Определитель через LU разложение
    public double determinant(){

        if(L == null || U == null || P == null)
            luDecomposition(this);

        double result = 1;
        for(int i = 0; i < cols; ++i)
            result *= U[i][i];

        return result;
    }

    //Нахождение обратной матрицы через LU разложение
    public static Matrix inverse(Matrix mat){

        Double[][] answer = new Double[mat.cols][mat.rows];
        Double[][] E = genE(mat.cols).getData();

        for(int i = 0; i < mat.rows; ++i)
            answer[i] = solveLU(mat, E[i]);

        return new Matrix(answer).transpose();
    }

    public static double[] solveTridiagonal(Matrix mat, Double[] d) {

        int n = mat.rows;
        double[] P = new double[n];
        double[] Q = new double[n];
        double[] x = new double[n];

        //Окно для обработки матрицы
        int a = -1,b = 0,c = 1;

        //Вычисления P и Q для каждой строки
        P[0] = -mat.data[0][c] / mat.data[0][b];
        Q[0] = d[0] / mat.data[0][b];

        for (int i = 1; i < n - 1; i++) {
            ++a; ++b; ++c;
            double denom = mat.data[i][b] + mat.data[i][a] * P[i - 1];
            P[i] = -mat.data[i][c] / denom;
            Q[i] = (d[i] - mat.data[i][a] * Q[i - 1]) / denom;
        }

        ++a; ++b;
        Q[n - 1] = (d[n - 1] - mat.data[n - 1][a] * Q[n - 2]) / (mat.data[n - 1][b] + mat.data[n - 1][a] * P[n - 2]);
        x[n - 1] = Q[n - 1];

        //Вычисление x
        for (int i = n - 2; i >= 0; i--) {
            x[i] = P[i] * x[i + 1] + Q[i];
        }

        return x;
    }

    public static double[] solveSimpleIterations(Matrix mat, Double[] b, double tol, int maxIter) {

        double[] B = new double[mat.rows];
        double[][] A = new double[mat.rows][mat.cols];

        //Заполение коэффициентов системы
        for(int i = 0; i < mat.rows; ++i){
            B[i] = b[i] / mat.data[i][i];
            for(int j = 0; j < mat.cols; ++j){
                A[i][j] = j == i ? 0 : -mat.data[i][j] / mat.data[i][i];
            }
        }

        //Нахождение альфы для вычисления значения точности итерации
        double a = 0;
        for(int j = 0; j  < mat.cols; ++j){
            double sum = 0;
            for(int i = 0; i < mat.rows; ++i){
                sum += Math.abs(A[i][j]);
            }
            a = Math.max(a, sum);
        }

        //Начальные x
        double[] x = Arrays.copyOf(B, B.length);

        //Итерации с ограничением
        for(int iter = 0; iter < maxIter; ++iter){

            double[] newX = new double[mat.rows];
            for(int i = 0; i < mat.rows; ++i){
                double sum = 0;
                for(int j = 0; j < mat.cols; ++j){
                    sum += A[i][j] * x[j];
                }
                newX[i] = sum + B[i];
            }

            //Вычсиление ошибки (т.е. точности)
            double error = 0;
            for(int i = 0; i < mat.rows; ++i){
                error = Math.max(error, Math.abs((a/(1 - a))*(newX[i] - x[i])));
            }

            x = newX;

            if(error <= tol) {
                break;
            }

        }

        return x;

    }

    public static double[] solveSeidel(Matrix mat, Double[] b, double tol, int maxIter) {

        double[] B = new double[mat.rows];
        double[][] A = new double[mat.rows][mat.cols];
        for(int i = 0; i < mat.rows; ++i){
            B[i] = b[i] / mat.data[i][i];
            for(int j = 0; j < mat.cols; ++j){
                A[i][j] = j == i ? 0 : -mat.data[i][j] / mat.data[i][i];
            }
        }

        double a = 0;
        for(int j = 0; j  < mat.cols; ++j){
            double sum = 0;
            for(int i = 0; i < mat.rows; ++i){
                sum += Math.abs(A[i][j]);
            }
            a = Math.max(a, sum);
        }

        double[] x = Arrays.copyOf(B, B.length);

        for(int iter = 0; iter < maxIter; ++iter){

            double[] newX = new double[mat.rows];
            for(int i = 0; i < mat.rows; ++i){
                double sum = 0;
                for(int j = 0; j < mat.cols; ++j){
                    sum += j < i ? A[i][j]*newX[j] : A[i][j] * x[j];
                }
                newX[i] = sum + B[i];
            }

            double error = 0;
            for(int i = 0; i < mat.rows; ++i){
                error = Math.max(error, Math.abs((a/(1 - a))*(newX[i] - x[i])));
            }

            x = newX;

            if(error <= tol) {
                break;
            }

        }

        return x;

    }

    public static Matrix[] getSZSV(Matrix mat, double tol, int maxIter){

        Double[][] A = mat.getData();
        //Матрица для нахождения значений СВ
        Matrix ansU = null;
        //Итерация с ограничением
        for(int iter = 0; iter < maxIter; ++iter){

            int[] ij = new int[2];
            double max = 0;
            for(int i = 0; i < mat.rows; ++i){
                for(int j = 0; j < mat.cols; ++j){
                    if(i < j && max < Math.abs(A[i][j])){
                        max = Math.abs(A[i][j]);
                        ij[0] = i; ij[1] = j;
                    }
                }
            }

            int i = ij[0];
            int j = ij[1];

            double phi = A[i][i] == A[j][j] ? Math.PI / 4 : Math.atan(2*(A[i][j]/(A[i][i] - A[j][j])))/2;

            //Генерируем матрицу вращения
            Double[][] U = genE(mat.rows).getData();
            U[i][i] = Math.cos(phi);
            U[j][j] = Math.cos(phi);
            U[i][j] = -Math.sin(phi);
            U[j][i] = Math.sin(phi);

            Matrix mA = new Matrix(A), mU = new Matrix(U);
            if(ansU == null){
                ansU = mU;
            }
            else{
                ansU = ansU.multiply(mU);
            }
            A = mU.transpose().multiply(mA).multiply(mU).getData();

            //Вычисления критерия остановки
            double sum = 0;
            for(int r = 0; r < mat.rows; ++r){
                for(int c = 0; c < mat.cols; ++c){
                    if(r < c){
                        sum += A[r][c]*A[r][c];
                    }
                }
            }
            if(sum < tol){
                break;
            }

        }

        return new Matrix[]{new Matrix(A), ansU};

    }

    @Override
    public String toString() {
        return Arrays.deepToString(data).replace("],", "]\n");
    }
}

