import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Integration.*;
import org.example.Interpolation.CubicSplineInterpolator;
import org.example.Interpolation.InterpolationPoint;
import org.example.Interpolation.Interpolator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class IntegrationTest {

    static class TestCase{
        @JsonProperty("X0")
        double X0;
        @JsonProperty("Xk")
        double Xk;
        @JsonProperty("h1")
        double h1;
        @JsonProperty("h2")
        double h2;
    }

    Function<Double, Double> function = (x) -> Math.pow(x, 2) / (Math.pow(x,2) + 16);

    static Stream<TestCase> provideTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = IntegrationTest.class.getClassLoader().getResourceAsStream("Integration/test1.json");
        List<TestCase> testCases = mapper.readValue(is, new TypeReference<>() {});
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testAddition(TestCase testCase) { //TODO: добавить вычисление k + ошибка при неправильном шаге
        NumericalIntegrator rectInt = new RectangleIntegrator();
        NumericalIntegrator trapInt = new TrapezoidIntegrator();
        NumericalIntegrator simpInt = new SimpsonIntegrator();

        System.out.printf("Значение определенного интеграла на интервале [%f, %f] вычисленнго методом прямоугольников с шагом %f: %f\n",
                testCase.X0,
                testCase.Xk,
                testCase.h1,
                rectInt.integrate(function, testCase.X0, testCase.Xk, testCase.h1)
        );
        System.out.printf("Значение определенного интеграла на интервале [%f, %f] вычисленнго методом прямоугольников с шагом %f: %f\n",
                testCase.X0,
                testCase.Xk,
                testCase.h2,
                rectInt.integrate(function, testCase.X0, testCase.Xk, testCase.h2)
        );
        System.out.printf("Уточненный интеграл на основе метода Рунге-Ромберга: %f\n\n", ErrorEstimator.estimateRungeRombergError(
                rectInt.integrate(function, testCase.X0, testCase.Xk, testCase.h1),
                rectInt.integrate(function, testCase.X0, testCase.Xk, testCase.h2),
                testCase.h1 / testCase.h2,
                2
        ));

        System.out.printf("Значение определенного интеграла на интервале [%f, %f] вычисленнго методом трапеций с шагом %f: %f\n",
                testCase.X0,
                testCase.Xk,
                testCase.h1,
                trapInt.integrate(function, testCase.X0, testCase.Xk, testCase.h1)
        );
        System.out.printf("Значение определенного интеграла на интервале [%f, %f] вычисленнго методом трапеций с шагом %f: %f\n",
                testCase.X0,
                testCase.Xk,
                testCase.h2,
                trapInt.integrate(function, testCase.X0, testCase.Xk, testCase.h2)
        );
        System.out.printf("Уточненный интеграл на основе метода Рунге-Ромберга: %f\n\n", ErrorEstimator.estimateRungeRombergError(
                trapInt.integrate(function, testCase.X0, testCase.Xk, testCase.h1),
                trapInt.integrate(function, testCase.X0, testCase.Xk, testCase.h2),
                testCase.h1 / testCase.h2,
                2
        ));

        System.out.printf("Значение определенного интеграла на интервале [%f, %f] вычисленнго методом Симпсона с шагом %f: %f\n",
                testCase.X0,
                testCase.Xk,
                testCase.h1,
                simpInt.integrate(function, testCase.X0, testCase.Xk, testCase.h1)
        );
        System.out.printf("Значение определенного интеграла на интервале [%f, %f] вычисленнго методом Симпсона с шагом %f: %f\n",
                testCase.X0,
                testCase.Xk,
                testCase.h2,
                simpInt.integrate(function, testCase.X0, testCase.Xk, testCase.h2)
        );
        System.out.printf("Уточненный интеграл на основе метода Рунге-Ромберга: %f\n\n", ErrorEstimator.estimateRungeRombergError(
                simpInt.integrate(function, testCase.X0, testCase.Xk, testCase.h1),
                simpInt.integrate(function, testCase.X0, testCase.Xk, testCase.h2),
                testCase.h1 / testCase.h2,
                4
        ));

    }

}
