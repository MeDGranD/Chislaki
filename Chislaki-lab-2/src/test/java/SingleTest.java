import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.NewtonSolver;
import org.example.SimpleIterationSolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class SingleTest {

    static class TestCase{
        @JsonProperty("tolerance")
        double tolerance;
        @JsonProperty("maxIter")
        int maxIter;
        @JsonProperty("a")
        double a;
        @JsonProperty("b")
        double b;
        @JsonProperty("startSimple")
        double startSimple;
        @JsonProperty("startNewton")
        double startNewton;
    }

    Function<Double, Double> func = (x) -> Math.sqrt((Math.sin(x) + 0.5) / 2);
    Function<Double, Double> funcN = (x) -> Math.sin(x) - 2 * x * x + 0.5;

    static Stream<TestCase> provideTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = SingleTest.class.getClassLoader().getResourceAsStream("test1.json");
        List<TestCase> testCases = mapper.readValue(is, new TypeReference<>() {});
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testAddition(TestCase testCase) { //TODO: выводить кол-во итераций и проверка

        double xVal = SimpleIterationSolver.getRoot(
                func,
                testCase.a,
                testCase.b,
                testCase.startSimple,
                testCase.tolerance,
                testCase.maxIter
        );

        System.out.println(xVal);

        xVal = NewtonSolver.getRoot(
                funcN,
                testCase.startNewton,
                testCase.tolerance,
                testCase.maxIter
        );

        System.out.println(xVal);

    }

}
