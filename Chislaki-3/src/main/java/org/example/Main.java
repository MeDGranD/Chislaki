package org.example;

import org.example.Integration.*;
import org.example.Interpolation.CubicSplineInterpolator;
import org.example.Interpolation.InterpolationPoint;
import org.example.Interpolation.LagrangeInterpolator;
import org.example.Interpolation.NewtonInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {

    private static final Function<Double, Double> func = Math::atan;

    public static void main(String[] args) {}
}