package org.example.Interpolation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LagrangeInterpolator implements Interpolator{

    @NonNull
    private final List<InterpolationPoint> points;

    @Override
    public double interpolate(double x) { //TODO: добавить проверки на вхождение

        if(x < points.getFirst().getX() || x > points.getLast().getX())
            throw new IllegalArgumentException(String.format("Значение x должно быть в пределах от %f до %f", points.getFirst().getX(), points.getLast().getX()));

        double lagrangePolynomialValue = 0.0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            double basisPolynomial = calculateBasisPolynomial(x, i);
            lagrangePolynomialValue += points.get(i).getY() * basisPolynomial;
        }

        return lagrangePolynomialValue;
    }

    private double calculateBasisPolynomial(double x, int i) {

        double basisPolynomialValue = 1.0;
        int n = points.size();
        double xi = points.get(i).getX();

        for (int j = 0; j < n; j++) {
            if (i == j) {
                continue;
            }
            double xj = points.get(j).getX();

            if (Math.abs(xi - xj) < 1e-15) {
                throw new ArithmeticException("Duplicate x values found in interpolation points at index " + i + " and " + j + ". Cannot compute Lagrange basis polynomial.");
            }

            basisPolynomialValue *= (x - xj) / (xi - xj);
        }
        return basisPolynomialValue;
    }

    @Override
    public void printPolynomial() {
        StringBuilder polynomial = new StringBuilder();
        int n = points.size();

        for (int i = 0; i < n; i++) {
            InterpolationPoint pi = points.get(i);
            double yi = pi.getY();
            double xi = pi.getX();

            StringBuilder basisTerm = new StringBuilder();
            basisTerm.append(String.format("%.4f * (", yi));

            boolean first = true;
            for (int j = 0; j < n; j++) {
                if (j == i) continue;

                double xj = points.get(j).getX();
                double denominator = xi - xj;
                if (!first) basisTerm.append(" * ");
                basisTerm.append(String.format("(x - %.4f)/%.4f", xj, denominator));
                first = false;
            }

            basisTerm.append(")");

            if (i > 0) {
                polynomial.append(" + ");
            }
            polynomial.append(basisTerm);
        }

        System.out.println("Lagrange interpolation polynomial:");
        System.out.println(polynomial);
    }

}
