import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.linear.*;
import org.example.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class MatrixTests {

    static Stream<String> LUjsonFiles(){
        return Stream.of(
                "src/main/resources/tests/LU/matrix1.json",
                "src/main/resources/tests/LU/matrix2.json",
                "src/main/resources/tests/LU/matrix3.json"
        );
    }

    static Stream<String> TriagonaljsonFiles(){
        return Stream.of(
                "src/main/resources/tests/Triagonal/matrix1.json",
                "src/main/resources/tests/Triagonal/matrix2.json",
                "src/main/resources/tests/Triagonal/matrix3.json"
        );
    }

    static Stream<String> IterationjsonFiles(){
        return Stream.of(
                "src/main/resources/tests/Iteration/matrix1.json",
                "src/main/resources/tests/Iteration/matrix2.json",
                "src/main/resources/tests/Iteration/matrix3.json"
        );
    }

    static Stream<String> SZSVjsonFiles(){
        return Stream.of(
                "src/main/resources/tests/SZSV/matrix1.json",
                "src/main/resources/tests/SZSV/matrix2.json",
                "src/main/resources/tests/SZSV/matrix3.json"
        );
    }

    static Stream<String> QRjsonFiles(){
        return Stream.of(
                "src/main/resources/tests/qr/matrix1.json",
                "src/main/resources/tests/qr/matrix2.json",
                "src/main/resources/tests/qr/matrix3.json"
        );
    }

    @ParameterizedTest(name = "LU test for {0}")
    @MethodSource("LUjsonFiles")
    public void LUTest2(String filepath) throws IOException {
        MatrixJSONLoader.SLAU SLAU = MatrixJSONLoader.loadSLAU(filepath);
        LUResolver resolver = new LUResolver(SLAU.matrix);
        RealMatrix[] mats = resolver.getLU();
        RealVector answer = resolver.solveLU(SLAU.b);

        System.out.println("Заданная матрица:\n" + matrixToString(resolver.getData()) + '\n');
        System.out.println("Правая часть:\n"+Arrays.toString(SLAU.b) + "\n");
        System.out.println("L матрица:\n" + matrixToString(mats[0]) + '\n');
        System.out.println("U матрица:\n" + matrixToString(mats[1]) + '\n');
        System.out.println("Корни x: "+ vectorToString(answer) + '\n');
        System.out.println(vectorToString(resolver.getData().operate(answer)) + '\n');
        System.out.println("Обратная матрица:\n" + matrixToString(resolver.inverse()) + '\n');
        System.out.println(matrixToString(resolver.getData().multiply(resolver.inverse())) + '\n');
        System.out.println("Определитель матрицы: " + resolver.determinant());
    }

    @ParameterizedTest(name = "Triagonal test for {0}")
    @MethodSource("TriagonaljsonFiles")
    public void TriagonalTest2(String filepath) throws IOException {
        MatrixJSONLoader.SLAU SLAU = MatrixJSONLoader.loadSLAU(filepath);
        RealMatrix mat = new Array2DRowRealMatrix(SLAU.matrix, true);
        RealVector answer = TriagonalResolver.solveTridiagonal(mat, SLAU.b);

        System.out.println("Заданная матрица:\n" + matrixToString(mat) + '\n');
        System.out.println("Правая часть:\n"+Arrays.toString(SLAU.b) + "\n");
        System.out.println("Корни x: " + vectorToString(answer));
        System.out.println(vectorToString(mat.operate(answer)) + '\n');

    }

    @ParameterizedTest(name = "Seidel + simple iteration test for {0}")
    @MethodSource("IterationjsonFiles")
    public void IterationTest2(String filepath) throws IOException {
        MatrixJSONLoader.SLAU SLAU = MatrixJSONLoader.loadSLAU(filepath);
        MatrixJSONLoader.Accuracy accuracy = MatrixJSONLoader.loadAccuracy(filepath);
        RealMatrix mat = new Array2DRowRealMatrix(SLAU.matrix);
        RealVector answerSimple = IterationResolver.solveSimpleIterations(mat, SLAU.b, accuracy.tol, accuracy.maxIter);
        RealVector answerSeidel = IterationResolver.solveSeidel(mat, SLAU.b, accuracy.tol, accuracy.maxIter);

        System.out.println("Заданная матрица:\n" + matrixToString(mat) + '\n');
        System.out.println("Правая часть:\n"+Arrays.toString(SLAU.b) + "\n");
        System.out.println("Корни x(простыми итерациями): " + vectorToString(answerSimple));
        System.out.println(vectorToString(mat.operate(answerSimple)) + '\n');
        System.out.println("Корни x(Зейдель): " + vectorToString(answerSeidel));
        System.out.println(vectorToString(mat.operate(answerSeidel)) + '\n');
    }

    @ParameterizedTest(name = "SZSV test for {0}")
    @MethodSource("SZSVjsonFiles")
    public void testSZSVDiagonalization(String filepath) throws IOException {
        MatrixJSONLoader.SLAU slaData = MatrixJSONLoader.loadSLAU(filepath);
        MatrixJSONLoader.Accuracy accuracy = MatrixJSONLoader.loadAccuracy(filepath);

        RealMatrix originalMatrix = new Array2DRowRealMatrix(slaData.matrix);

        RealMatrix[] result = SZSVResolver.getSZSV(originalMatrix, accuracy.tol, accuracy.maxIter);
        RealMatrix eigenvalueMatrix = result[0];
        RealMatrix eigenvectorMatrix = result[1];

        System.out.println("\nИсходная матрица:");
        System.out.println(matrixToString(originalMatrix));
        System.out.println("\nСобственные значения:");
        for (int i = 0; i < eigenvalueMatrix.getRowDimension(); i++) {
            System.out.printf("λ%d = %.6f%n", i+1, eigenvalueMatrix.getEntry(i, i));
        }
        System.out.println("\nСобственные векторы (транспонированные столбцы):");
        for (int i = 0; i < eigenvalueMatrix.getRowDimension(); i++) {
            System.out.println(vectorToString(eigenvectorMatrix.getColumnVector(i)));
            System.out.println("----------------------------------");
        }

        verifyEigenDecomposition(originalMatrix, eigenvalueMatrix, eigenvectorMatrix, accuracy.tol);
    }

    private void verifyEigenDecomposition(RealMatrix A, RealMatrix D, RealMatrix V, double tolerance) {
        for (int i = 0; i < A.getColumnDimension(); i++) {
            // Получаем i-й собственный вектор
            RealVector eigenvector = V.getColumnVector(i);
            // Получаем i-е собственное значение
            double eigenvalue = D.getEntry(i, i);

            // Вычисляем A*v
            RealVector Av = A.operate(eigenvector);
            // Вычисляем λ*v
            RealVector lambdaV = eigenvector.mapMultiply(eigenvalue);

            // Проверяем равенство A*v = λ*v с заданной точностью
            double error = Av.subtract(lambdaV).getNorm();
            if (error > tolerance) {
                System.err.printf("Ошибка проверки для %d-го собственного значения: %.6e > %.6e%n",
                        i+1, error, tolerance);
            }

            // Дополнительный вывод для анализа
            System.out.printf("%nПроверка для λ%d = %.6f:%n", i+1, eigenvalue);
            System.out.println("A*v:    " + vectorToString(Av));
            System.out.println("λ*v:    " + vectorToString(lambdaV));
            System.out.printf("Ошибка: %.6e%n", error);
        }
    }

    @ParameterizedTest(name = "QR test for {0}")
    @MethodSource("QRjsonFiles")
    public void QRTest(String filepath) throws IOException {
        MatrixJSONLoader.SLAU slaData = MatrixJSONLoader.loadSLAU(filepath);
        MatrixJSONLoader.Accuracy accuracy = MatrixJSONLoader.loadAccuracy(filepath);

        RealMatrix mat = new Array2DRowRealMatrix(slaData.matrix);

        QRResolver.QRResult qr = QRResolver.householderQR(mat);

        System.out.println("\nИсходная матрица:");
        System.out.println(matrixToString(mat));
        System.out.println("\nQ матрица:");
        System.out.println(matrixToString(qr.Q));
        System.out.println("\nR матрица:");
        System.out.println(matrixToString(qr.R));
        System.out.println("\nСобственные значения:");
        System.out.println(QRResolver.getEigenValuesQR(mat,  accuracy.tol));
    }

    private String matrixToString(RealMatrix matrix) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix.getData()) {
            sb.append(Arrays.toString(row)).append('\n');
        }
        return sb.toString();
    }

    private String vectorToString(RealVector vector) {
        return Arrays.toString(vector.toArray());
    }

}
