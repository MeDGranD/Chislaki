import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.SystemNewtonSolver;
import org.example.SystemSimpleIterationSolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class SystemTest {

    static class TestCase{
        @JsonProperty("tolerance")
        double tolerance;
        @JsonProperty("maxIter")
        int maxIter;
        @JsonProperty("startSimple")
        double[] startSimple;
        @JsonProperty("startNewton")
        double[] startNewton;
    }

    Function<double[], Double> func1 = (x) -> x[0] - Math.cos(x[1]) - 1;
    Function<double[], Double> func2 = (x) -> x[1] - Math.sin(x[0]) - 1;

    Function<double[], Double> func1S = (x) -> Math.cos(x[1]) + 1;
    Function<double[], Double> func2S = (x) -> Math.sin(x[0]) + 1;

    static Stream<TestCase> provideTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = SystemTest.class.getClassLoader().getResourceAsStream("test2.json");
        List<TestCase> testCases = mapper.readValue(is, new TypeReference<>() {});
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testAddition(TestCase testCase){ //TODO: также выводить кол-во итераций и q
        //Добавить отрез и для систем

        @SuppressWarnings("unchecked")
        double[] ans = SystemNewtonSolver.getRoots(
                testCase.startNewton,
                new Function[]{func1, func2},
                testCase.tolerance,
                testCase.maxIter
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

        @SuppressWarnings("unchecked")
        double[] ansS = SystemSimpleIterationSolver.getRoots(
                new Function[]{func1S, func2S},
                testCase.startSimple,
                testCase.tolerance,
                testCase.maxIter
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
