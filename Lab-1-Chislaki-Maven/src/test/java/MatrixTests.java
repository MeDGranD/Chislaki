import org.example.Matrix;
import org.example.MatrixJSONLoader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
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

    @ParameterizedTest(name = "LU test")
    @MethodSource("LUjsonFiles")
    public void LUTest(String filepath) throws IOException {
        MatrixJSONLoader.SLAU SLAU = MatrixJSONLoader.loadSLAU(filepath);
        Matrix mat = new Matrix(SLAU.matrix);
        Matrix[] mats = Matrix.luDecomposition(mat);
        Double[] answer = Matrix.solveLU(mat, SLAU.b);

        System.out.println("Заданная матрица:\n" + mat + '\n');
        System.out.println("L матрица:\n" + mats[0] + '\n');
        System.out.println("U матрица:\n" + mats[1] + '\n');
        System.out.println("Корни x: "+ Arrays.toString(answer) + '\n');
        System.out.println("Обратная матрица:\n" + Matrix.inverse(mat) + '\n');
        System.out.println("Определитель матрицы: " + mat.determinant());
    }

    @ParameterizedTest(name = "Triagonal test")
    @MethodSource("TriagonaljsonFiles")
    public void TriagonalTest(String filepath) throws IOException {
        MatrixJSONLoader.SLAU SLAU = MatrixJSONLoader.loadSLAU(filepath);
        Matrix mat = new Matrix(SLAU.matrix);
        double[] answer = Matrix.solveTridiagonal(mat, SLAU.b);

        System.out.println("Заданная матрица:\n" + mat + '\n');
        System.out.println("Корни x: " + Arrays.toString(answer));

    }

    @ParameterizedTest(name = "Seidel + simple iteration test")
    @MethodSource("IterationjsonFiles")
    public void IterationTest(String filepath) throws IOException {
        MatrixJSONLoader.SLAU SLAU = MatrixJSONLoader.loadSLAU(filepath);
        MatrixJSONLoader.Accuracy accuracy = MatrixJSONLoader.loadAccuracy(filepath);
        Matrix mat = new Matrix(SLAU.matrix);
        double[] answerSimple = Matrix.solveSimpleIterations(mat, SLAU.b, accuracy.tol, accuracy.maxIter);
        double[] answerSeidel = Matrix.solveSeidel(mat, SLAU.b, accuracy.tol, accuracy.maxIter);

        System.out.println("Заданная матрица:\n" + mat + '\n');
        System.out.println("Корни x(простыми итерациями): " + Arrays.toString(answerSimple));
        System.out.println("Корни x(Зейдель): " + Arrays.toString(answerSeidel));
    }

    @ParameterizedTest(name = "SZSV test")
    @MethodSource("SZSVjsonFiles")
    public void SZSVTest(String filepath) throws IOException {
        MatrixJSONLoader.SLAU SLAU = MatrixJSONLoader.loadSLAU(filepath);
        MatrixJSONLoader.Accuracy accuracy = MatrixJSONLoader.loadAccuracy(filepath);
        Matrix mat = new Matrix(SLAU.matrix);
        Matrix[] answer = Matrix.getSZSV(mat, accuracy.tol, accuracy.maxIter);

        System.out.println("Заданная матрица:\n" + mat + '\n');
        System.out.println("Матрица собственных значений:\n" + answer[0] + '\n');
        System.out.println("Матрица собственных векторов:\n" + answer[1] + '\n');
    }

}
