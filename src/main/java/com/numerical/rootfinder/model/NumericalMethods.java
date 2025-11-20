package com.numerical.rootfinder.model;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.ArrayList;
import java.util.List;

public class NumericalMethods {

    private Expression function;
    private double tolerance;
    private int maxIterations;

    public NumericalMethods(String equation, double tolerance, int maxIterations) {
        try {
            this.function = new ExpressionBuilder(equation)
                    .variables("x")
                    .build();
            this.tolerance = tolerance;
            this.maxIterations = maxIterations;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing equation: " + e.getMessage());
        }
    }

    public double evaluate(double x) {
        try {
            return function.setVariable("x", x).evaluate();
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating function at x=" + x + ": " + e.getMessage());
        }
    }

    public MethodResult bisection(double a, double b) {
        List<IterationResult> iterations = new ArrayList<>();
        Double root = null;
        String errorMessage = null;
        boolean converged = false;

        double fa = evaluate(a);
        double fb = evaluate(b);

        if (fa * fb >= 0) {
            errorMessage = "f(a) and f(b) must have opposite signs for bisection method.";
            return new MethodResult(RootFindingMethod.BISECTION, iterations, root, errorMessage, false);
        }

        for (int i = 0; i < maxIterations; i++) {
            double c = (a + b) / 2;
            double fc = evaluate(c);

            IterationResult iteration = new IterationResult(i + 1);
            iteration.setA(a);
            iteration.setB(b);
            iteration.setRoot(c);
            iteration.setfRoot(fc);

            if (i > 0) {
                double prevRoot = iterations.get(i - 1).getRoot();
                iteration.setError(Math.abs((c - prevRoot) / c) * 100);
            }

            iterations.add(iteration);

            if (Math.abs(fc) < tolerance || Math.abs(b - a) < tolerance) {
                root = c;
                converged = true;
                break;
            }

            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }

        return new MethodResult(RootFindingMethod.BISECTION, iterations, root, errorMessage, converged);
    }

    public MethodResult falsePosition(double a, double b) {
        List<IterationResult> iterations = new ArrayList<>();
        Double root = null;
        String errorMessage = null;
        boolean converged = false;

        double fa = evaluate(a);
        double fb = evaluate(b);

        if (fa * fb >= 0) {
            errorMessage = "f(a) and f(b) must have opposite signs for false position method.";
            return new MethodResult(RootFindingMethod.FALSE_POSITION, iterations, root, errorMessage, false);
        }

        Double prevC = null;

        for (int i = 0; i < maxIterations; i++) {
            fa = evaluate(a);
            fb = evaluate(b);
            double c = (a * fb - b * fa) / (fb - fa);
            double fc = evaluate(c);

            IterationResult iteration = new IterationResult(i + 1);
            iteration.setA(a);
            iteration.setB(b);
            iteration.setRoot(c);
            iteration.setfRoot(fc);

            if (prevC != null) {
                iteration.setError(Math.abs((c - prevC) / c) * 100);
            }
            prevC = c;

            iterations.add(iteration);

            if (Math.abs(fc) < tolerance) {
                root = c;
                converged = true;
                break;
            }

            if (fa * fc < 0) {
                b = c;
            } else {
                a = c;
            }
        }

        return new MethodResult(RootFindingMethod.FALSE_POSITION, iterations, root, errorMessage, converged);
    }

    public MethodResult fixedPoint(double x0, String gFunction) {
        List<IterationResult> iterations = new ArrayList<>();
        Double root = null;
        String errorMessage = null;
        boolean converged = false;

        Expression g;
        try {
            g = new ExpressionBuilder(gFunction)
                    .variables("x")
                    .build();
        } catch (Exception e) {
            errorMessage = "Error parsing g(x): " + e.getMessage();
            return new MethodResult(RootFindingMethod.FIXED_POINT, iterations, root, errorMessage, false);
        }

        double prevX = x0;

        for (int i = 0; i < maxIterations; i++) {
            double xNew;
            try {
                xNew = g.setVariable("x", prevX).evaluate();
            } catch (Exception e) {
                errorMessage = "Error evaluating g(x) at x=" + prevX + ": " + e.getMessage();
                return new MethodResult(RootFindingMethod.FIXED_POINT, iterations, root, errorMessage, false);
            }

            IterationResult iteration = new IterationResult(i + 1);
            iteration.setX_i(prevX);
            iteration.setX_i1(xNew);
            iteration.setG_x(xNew);

            if (i > 0) {
                iteration.setError(Math.abs((xNew - prevX) / xNew) * 100);
            }

            iterations.add(iteration);

            if (Math.abs(xNew - prevX) < tolerance) {
                root = xNew;
                converged = true;
                break;
            }

            prevX = xNew;
        }

        return new MethodResult(RootFindingMethod.FIXED_POINT, iterations, root, errorMessage, converged);
    }

    public MethodResult newtonRaphson(double x0) {
        List<IterationResult> iterations = new ArrayList<>();
        Double root = null;
        String errorMessage = null;
        boolean converged = false;

        double h = 1e-8; // For numerical differentiation
        double xPrev = x0;

        for (int i = 0; i < maxIterations; i++) {
            try {
                double fVal = evaluate(xPrev);
                double fPrimeVal = (evaluate(xPrev + h) - evaluate(xPrev - h)) / (2 * h);

                if (Math.abs(fPrimeVal) < 1e-15) {
                    errorMessage = "Derivative too close to zero in Newton-Raphson method.";
                    return new MethodResult(RootFindingMethod.NEWTON_RAPHSON, iterations, root, errorMessage, false);
                }

                double xNew = xPrev - fVal / fPrimeVal;

                IterationResult iteration = new IterationResult(i + 1);
                iteration.setX_i(xPrev);
                iteration.setX_i1(xNew);
                iteration.setF_x(fVal);
                iteration.setF_prime_x(fPrimeVal);

                if (i > 0) {
                    iteration.setError(Math.abs((xNew - xPrev) / xNew) * 100);
                }

                iterations.add(iteration);

                if (Math.abs(xNew - xPrev) < tolerance) {
                    root = xNew;
                    converged = true;
                    break;
                }

                xPrev = xNew;
            } catch (Exception e) {
                errorMessage = "Error in iteration " + (i + 1) + ": " + e.getMessage();
                return new MethodResult(RootFindingMethod.NEWTON_RAPHSON, iterations, root, errorMessage, false);
            }
        }

        return new MethodResult(RootFindingMethod.NEWTON_RAPHSON, iterations, root, errorMessage, converged);
    }

    public MethodResult secant(double x0, double x1) {
        List<IterationResult> iterations = new ArrayList<>();
        Double root = null;
        String errorMessage = null;
        boolean converged = false;

        double xPrev2 = x0;
        double xPrev1 = x1;

        // First iteration
        IterationResult firstIteration = new IterationResult(1);
        firstIteration.setX_i1(xPrev2);
        firstIteration.setX_i(xPrev1);
        firstIteration.setF_x_i1(evaluate(xPrev2));
        firstIteration.setF_x_i(evaluate(xPrev1));
        iterations.add(firstIteration);

        for (int i = 1; i < maxIterations; i++) {
            try {
                double fPrev2 = evaluate(xPrev2);
                double fPrev1 = evaluate(xPrev1);

                if (Math.abs(fPrev1 - fPrev2) < 1e-15) {
                    errorMessage = "Division by zero in secant method.";
                    return new MethodResult(RootFindingMethod.SECANT, iterations, root, errorMessage, false);
                }

                double xNew = xPrev1 - fPrev1 * (xPrev1 - xPrev2) / (fPrev1 - fPrev2);
                double fNew = evaluate(xNew);

                IterationResult iteration = new IterationResult(i + 1);
                iteration.setX_i1(xPrev1);
                iteration.setX_i(xNew);
                iteration.setF_x_i1(fPrev1);
                iteration.setF_x_i(fNew);
                iteration.setError(Math.abs((xNew - xPrev1) / xNew) * 100);

                iterations.add(iteration);

                if (Math.abs(xNew - xPrev1) < tolerance) {
                    root = xNew;
                    converged = true;
                    break;
                }

                xPrev2 = xPrev1;
                xPrev1 = xNew;
            } catch (Exception e) {
                errorMessage = "Error in iteration " + (i + 1) + ": " + e.getMessage();
                return new MethodResult(RootFindingMethod.SECANT, iterations, root, errorMessage, false);
            }
        }

        return new MethodResult(RootFindingMethod.SECANT, iterations, root, errorMessage, converged);
    }
}