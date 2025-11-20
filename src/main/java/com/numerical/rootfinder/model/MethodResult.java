package com.numerical.rootfinder.model;

import java.util.List;

public class MethodResult {
    private RootFindingMethod method;
    private List<IterationResult> iterations;
    private Double root;
    private String errorMessage;
    private boolean converged;

    public MethodResult(RootFindingMethod method, List<IterationResult> iterations,
                        Double root, String errorMessage, boolean converged) {
        this.method = method;
        this.iterations = iterations;
        this.root = root;
        this.errorMessage = errorMessage;
        this.converged = converged;
    }

    // Getters and setters
    public RootFindingMethod getMethod() { return method; }
    public void setMethod(RootFindingMethod method) { this.method = method; }

    public List<IterationResult> getIterations() { return iterations; }
    public void setIterations(List<IterationResult> iterations) { this.iterations = iterations; }

    public Double getRoot() { return root; }
    public void setRoot(Double root) { this.root = root; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public boolean isConverged() { return converged; }
    public void setConverged(boolean converged) { this.converged = converged; }
}