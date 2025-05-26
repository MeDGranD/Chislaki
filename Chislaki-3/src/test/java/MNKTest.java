import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Interpolation.CubicSplineInterpolator;
import org.example.Interpolation.InterpolationPoint;
import org.example.Interpolation.Interpolator;
import org.example.LeastSquaresApproximator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MNKTest {

    static class TestCase{
        @JsonProperty("X")
        double[] X;
        @JsonProperty("Y")
        double[] Y;
    }

    static Stream<TestCase> provideTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = MNKTest.class.getClassLoader().getResourceAsStream("MNK/test1.json");
        List<TestCase> testCases = mapper.readValue(is, new TypeReference<>() {});
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testAddition(TestCase testCase) {
        List<InterpolationPoint> points3 = new ArrayList<>();
        int i = 0;
        for(double x : testCase.X){
            points3.add(new InterpolationPoint(x, testCase.Y[i++]));
        }
        LeastSquaresApproximator approximator2 = new LeastSquaresApproximator(points3, 2);
        LeastSquaresApproximator approximator3 = new LeastSquaresApproximator(points3, 3);

        System.out.printf("Коэффициенты разложения: %s\n", Arrays.toString(approximator2.getCoefs()));
        System.out.printf("Значение квадратичной ошибки: %s\n", approximator2.squareError());
        System.out.printf("\nКоэффициенты разложения: %s\n", Arrays.toString(approximator3.getCoefs()));
        System.out.printf("Значение квадратичной ошибки: %s\n", approximator3.squareError());

    }

}
