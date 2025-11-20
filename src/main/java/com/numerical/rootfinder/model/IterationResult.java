package com.numerical.rootfinder.model;

public class IterationResult {
    private int iteration;
    private Double a;
    private Double b;
    private Double root;
    private Double fRoot;
    private Double error;
    private Double x_i;
    private Double x_i1;
    private Double f_x;
    private Double f_prime_x;
    private Double g_x;
    private Double f_x_i1;  // Add this
    private Double f_x_i;   // Add this

    public IterationResult(int iteration) {
        this.iteration = iteration;
    }

    // Getters and setters
    public int getIteration() { return iteration; }
    public void setIteration(int iteration) { this.iteration = iteration; }

    public Double getA() { return a; }
    public void setA(Double a) { this.a = a; }

    public Double getB() { return b; }
    public void setB(Double b) { this.b = b; }

    public Double getRoot() { return root; }
    public void setRoot(Double root) { this.root = root; }

    public Double getfRoot() { return fRoot; }
    public void setfRoot(Double fRoot) { this.fRoot = fRoot; }

    public Double getError() { return error; }
    public void setError(Double error) { this.error = error; }

    public Double getX_i() { return x_i; }
    public void setX_i(Double x_i) { this.x_i = x_i; }

    public Double getX_i1() { return x_i1; }
    public void setX_i1(Double x_i1) { this.x_i1 = x_i1; }

    public Double getF_x() { return f_x; }
    public void setF_x(Double f_x) { this.f_x = f_x; }

    public Double getF_prime_x() { return f_prime_x; }
    public void setF_prime_x(Double f_prime_x) { this.f_prime_x = f_prime_x; }

    public Double getG_x() { return g_x; }
    public void setG_x(Double g_x) { this.g_x = g_x; }

    // Add these missing getters and setters
    public Double getF_x_i1() { return f_x_i1; }
    public void setF_x_i1(Double f_x_i1) { this.f_x_i1 = f_x_i1; }

    public Double getF_x_i() { return f_x_i; }
    public void setF_x_i(Double f_x_i) { this.f_x_i = f_x_i; }
}