import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Interpolation.InterpolationPoint;
import org.example.Interpolation.Interpolator;
import org.example.Interpolation.LagrangeInterpolator;
import org.example.Interpolation.NewtonInterpolator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;

public class InterpolationTest {

    static class TestCase{
        @JsonProperty("X")
        double[] X;
        @JsonProperty("xVal")
        double xVal;
    }

    Function<Double, Double> function = Math::atan;

    static Stream<TestCase> provideTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = InterpolationTest.class.getClassLoader().getResourceAsStream("Interpolation/test1.json");
        List<TestCase> testCases = mapper.readValue(is, new TypeReference<>() {});
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testAddition(TestCase testCase) {
        List<InterpolationPoint> points = new ArrayList<>();

        for(double x : testCase.X){
            points.add(new InterpolationPoint(x, function.apply(x)));
        }

        Interpolator LagInter = new LagrangeInterpolator(points);
        Interpolator NewInter = new NewtonInterpolator(points);

        double LPoint = LagInter.interpolate(testCase.xVal);
        double NPoint = NewInter.interpolate(testCase.xVal);
        double TruePoint = function.apply(testCase.xVal);

        System.out.println(TruePoint);
        LagInter.printPolynomial();
        System.out.println(LPoint);
        System.out.println(Math.abs(LPoint-TruePoint));
        NewInter.printPolynomial();
        System.out.println(NPoint);
        System.out.println(Math.abs(NPoint-TruePoint));

    }

}
