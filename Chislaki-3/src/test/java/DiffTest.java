import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Interpolation.InterpolationPoint;
import org.example.LeastSquaresApproximator;
import org.example.NumericDifferentiator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DiffTest {

    static class TestCase{
        @JsonProperty("X")
        double[] X;
        @JsonProperty("Y")
        double[] Y;
        @JsonProperty("xVal")
        double xVal;
    }

    static Stream<TestCase> provideTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = DiffTest.class.getClassLoader().getResourceAsStream("Diff/test1.json");
        List<TestCase> testCases = mapper.readValue(is, new TypeReference<>() {});
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testAddition(TestCase testCase) {
        List<InterpolationPoint> points = new ArrayList<>();
        int i = 0;
        for(double x : testCase.X){
            points.add(new InterpolationPoint(x, testCase.Y[i++]));
        }

        NumericDifferentiator differentiator = new NumericDifferentiator(points);

        System.out.println(Arrays.toString(differentiator.calculateCentralDerivativesAtNode(testCase.xVal)));

    }

}
