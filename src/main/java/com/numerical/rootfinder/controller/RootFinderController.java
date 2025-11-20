package com.numerical.rootfinder.controller;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import com.numerical.rootfinder.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;  // Add this import
import javafx.scene.text.Text;
import javafx.beans.property.SimpleStringProperty;  // Add this import
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class RootFinderController implements Initializable {

    @FXML private StackPane plotContainer;
    @FXML private TextField equationField;
    @FXML private TextField toleranceField;
    @FXML private TextField maxIterationsField;
    @FXML private ComboBox<RootFindingMethod> methodComboBox;
    @FXML private VBox methodParamsContainer;
    @FXML private Button calculateButton;

    // Results tab components
    @FXML private TabPane resultsTabPane;
    @FXML private Tab resultsTab;
    @FXML private Tab plotTab;
    @FXML private Tab comparisonTab;

    @FXML private TableView<IterationResult> resultsTable;
    @FXML private Text resultsSummary;

    @FXML private TableView<ComparisonData> comparisonTable;  // Changed from Map to ComparisonData

    // Method parameter fields
    private TextField bisectionA, bisectionB;
    private TextField falsePositionA, falsePositionB;
    private TextField fixedPointX0, fixedPointG;
    private TextField newtonX0;
    private TextField secantX0, secantX1;

    // Inner class for comparison data
    public static class ComparisonData {
        private String method;
        private String root;
        private String iterations;
        private String error;
        private String status;

        // Getters and setters
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }

        public String getRoot() { return root; }
        public void setRoot(String root) { this.root = root; }

        public String getIterations() { return iterations; }
        public void setIterations(String iterations) { this.iterations = iterations; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupMethodComboBox();
        setupResultsTable();
        setupComparisonTable();
        setupEventHandlers();
        setDefaultValues();
    }

    private void setupMethodComboBox() {
        methodComboBox.setItems(FXCollections.observableArrayList(RootFindingMethod.values()));
        methodComboBox.setValue(RootFindingMethod.ALL);
    }

    private void setupResultsTable() {
        // Columns will be dynamically set based on the method
    }

    private void setupComparisonTable() {
        // Setup comparison table columns
        TableColumn<ComparisonData, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMethod()));

        TableColumn<ComparisonData, String> rootCol = new TableColumn<>("Root");
        rootCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoot()));

        TableColumn<ComparisonData, String> iterationsCol = new TableColumn<>("Iterations");
        iterationsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIterations()));

        TableColumn<ComparisonData, String> errorCol = new TableColumn<>("Final Error %");
        errorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getError()));

        TableColumn<ComparisonData, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        comparisonTable.getColumns().addAll(methodCol, rootCol, iterationsCol, errorCol, statusCol);
    }

    private void setupEventHandlers() {
        methodComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateMethodParameters();
        });

        calculateButton.setOnAction(e -> calculateRoots());
    }

    private void setDefaultValues() {
        equationField.setText("x^3 - 2*x - 5");
        toleranceField.setText("0.000001");
        maxIterationsField.setText("100");
        updateMethodParameters();
    }

    private void updateMethodParameters() {
        methodParamsContainer.getChildren().clear();

        RootFindingMethod method = methodComboBox.getValue();

        if (method == RootFindingMethod.ALL) {
            createAllMethodParameters();
        } else {
            createSingleMethodParameters(method);
        }
    }

    private void createAllMethodParameters() {
        Label title = new Label("All Methods Parameters");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        methodParamsContainer.getChildren().add(title);

        // Bisection parameters
        Label bisectionLabel = new Label("Bisection Method:");
        bisectionLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 0 0;");
        methodParamsContainer.getChildren().add(bisectionLabel);

        HBox bisectionBox = new HBox(10);
        bisectionA = createTextField("-2", "a (lower bound)");
        bisectionB = createTextField("3", "b (upper bound)");
        bisectionBox.getChildren().addAll(createLabeledField("a:", bisectionA), createLabeledField("b:", bisectionB));
        methodParamsContainer.getChildren().add(bisectionBox);

        // False Position parameters
        Label falsePositionLabel = new Label("False Position Method:");
        falsePositionLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 0 0;");
        methodParamsContainer.getChildren().add(falsePositionLabel);

        HBox falsePositionBox = new HBox(10);
        falsePositionA = createTextField("-2", "a (lower bound)");
        falsePositionB = createTextField("3", "b (upper bound)");
        falsePositionBox.getChildren().addAll(createLabeledField("a:", falsePositionA), createLabeledField("b:", falsePositionB));
        methodParamsContainer.getChildren().add(falsePositionBox);

        // Fixed Point parameters
        Label fixedPointLabel = new Label("Fixed Point Iteration:");
        fixedPointLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 0 0;");
        methodParamsContainer.getChildren().add(fixedPointLabel);

        VBox fixedPointBox = new VBox(5);
        fixedPointX0 = createTextField("2", "Initial guess");
        fixedPointG = createTextField("(2*x + 5)^(1/3)", "g(x) for x = g(x)");
        fixedPointBox.getChildren().addAll(
                createLabeledField("Initial guess (x₀):", fixedPointX0),
                createLabeledField("g(x):", fixedPointG)
        );
        methodParamsContainer.getChildren().add(fixedPointBox);

        // Newton-Raphson parameters
        Label newtonLabel = new Label("Newton-Raphson Method:");
        newtonLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 0 0;");
        methodParamsContainer.getChildren().add(newtonLabel);

        newtonX0 = createTextField("2", "Initial guess");
        methodParamsContainer.getChildren().add(createLabeledField("Initial guess (x₀):", newtonX0));

        // Secant parameters
        Label secantLabel = new Label("Secant Method:");
        secantLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 0 0;");
        methodParamsContainer.getChildren().add(secantLabel);

        HBox secantBox = new HBox(10);
        secantX0 = createTextField("2", "First initial guess");
        secantX1 = createTextField("3", "Second initial guess");
        secantBox.getChildren().addAll(
                createLabeledField("x₀:", secantX0),
                createLabeledField("x₁:", secantX1)
        );
        methodParamsContainer.getChildren().add(secantBox);
    }
    private void plotFunction(String equation, List<Double> roots) {
        plotContainer.getChildren().clear();

        try {
            // Create axes
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("x");
            yAxis.setLabel("f(x)");

            // Create line chart
            final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Function: " + equation);
            lineChart.setCreateSymbols(true);
            lineChart.setLegendVisible(true);

            // Determine plot range
            double xMin = -5, xMax = 5;
            if (roots != null && !roots.isEmpty()) {
                double minRoot = roots.stream().mapToDouble(Double::doubleValue).min().orElse(-5);
                double maxRoot = roots.stream().mapToDouble(Double::doubleValue).max().orElse(5);
                xMin = minRoot - 2;
                xMax = maxRoot + 2;
            }

            // Create function series
            XYChart.Series<Number, Number> functionSeries = new XYChart.Series<>();
            functionSeries.setName("f(x)");

            NumericalMethods tempSolver = new NumericalMethods(equation, 0.0001, 100);
            int points = 100;
            double step = (xMax - xMin) / points;

            for (int i = 0; i <= points; i++) {
                double x = xMin + i * step;
                try {
                    double y = tempSolver.evaluate(x);
                    if (!Double.isNaN(y) && !Double.isInfinite(y) && Math.abs(y) < 1e6) {
                        functionSeries.getData().add(new XYChart.Data<>(x, y));
                    }
                } catch (Exception e) {
                    // Skip points where function is undefined
                }
            }

            lineChart.getData().add(functionSeries);

            // Add zero line
            XYChart.Series<Number, Number> zeroSeries = new XYChart.Series<>();
            zeroSeries.setName("y = 0");
            zeroSeries.getData().add(new XYChart.Data<>(xMin, 0));
            zeroSeries.getData().add(new XYChart.Data<>(xMax, 0));
            lineChart.getData().add(zeroSeries);

            // Add root markers
            if (roots != null && !roots.isEmpty()) {
                for (int i = 0; i < roots.size(); i++) {
                    Double root = roots.get(i);
                    if (root != null) {
                        XYChart.Series<Number, Number> rootSeries = new XYChart.Series<>();
                        rootSeries.setName("Root " + (i + 1));
                        rootSeries.getData().add(new XYChart.Data<>(root, 0.0));
                        lineChart.getData().add(rootSeries);
                    }
                }
            }

            // Style the chart
            lineChart.setPrefSize(600, 400);
            lineChart.setStyle("-fx-background-color: white;");

            plotContainer.getChildren().add(lineChart);

        } catch (Exception e) {
            // Fallback to simple text display if chart fails
            showSimplePlot(equation, roots);
        }
    }

    // Fallback method if chart fails
    private void showSimplePlot(String equation, List<Double> roots) {
        plotContainer.getChildren().clear();

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label title = new Label("Function: " + equation);
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        content.getChildren().add(title);

        if (roots != null && !roots.isEmpty()) {
            Label rootsTitle = new Label("Roots Found:");
            rootsTitle.setStyle("-fx-font-weight: bold;");
            content.getChildren().add(rootsTitle);

            for (int i = 0; i < roots.size(); i++) {
                if (roots.get(i) != null) {
                    Label rootLabel = new Label(String.format("  Root %d: x = %.6f", i + 1, roots.get(i)));
                    rootLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    content.getChildren().add(rootLabel);
                }
            }
        }

        Label note = new Label("\nNote: Graphical plot unavailable. Showing results instead.");
        note.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
        content.getChildren().add(note);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        plotContainer.getChildren().add(scrollPane);
    }
    private void createSingleMethodParameters(RootFindingMethod method) {
        Label title = new Label(method.getDisplayName() + " Parameters");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        methodParamsContainer.getChildren().add(title);

        switch (method) {
            case BISECTION:
                createBisectionParameters();
                break;
            case FALSE_POSITION:
                createFalsePositionParameters();
                break;
            case FIXED_POINT:
                createFixedPointParameters();
                break;
            case NEWTON_RAPHSON:
                createNewtonRaphsonParameters();
                break;
            case SECANT:
                createSecantParameters();
                break;
        }
    }

    private void createBisectionParameters() {
        HBox paramsBox = new HBox(10);
        bisectionA = createTextField("-2", "Lower bound");
        bisectionB = createTextField("3", "Upper bound");
        paramsBox.getChildren().addAll(createLabeledField("a:", bisectionA), createLabeledField("b:", bisectionB));
        methodParamsContainer.getChildren().add(paramsBox);
    }

    private void createFalsePositionParameters() {
        HBox paramsBox = new HBox(10);
        falsePositionA = createTextField("-2", "Lower bound");
        falsePositionB = createTextField("3", "Upper bound");
        paramsBox.getChildren().addAll(createLabeledField("a:", falsePositionA), createLabeledField("b:", falsePositionB));
        methodParamsContainer.getChildren().add(paramsBox);
    }

    private void createFixedPointParameters() {
        VBox paramsBox = new VBox(5);
        fixedPointX0 = createTextField("2", "Initial guess");
        fixedPointG = createTextField("(2*x + 5)^(1/3)", "g(x) for x = g(x)");
        paramsBox.getChildren().addAll(
                createLabeledField("Initial guess (x₀):", fixedPointX0),
                createLabeledField("g(x):", fixedPointG)
        );
        methodParamsContainer.getChildren().add(paramsBox);
    }

    private void createNewtonRaphsonParameters() {
        newtonX0 = createTextField("2", "Initial guess");
        methodParamsContainer.getChildren().add(createLabeledField("Initial guess (x₀):", newtonX0));
    }

    private void createSecantParameters() {
        HBox paramsBox = new HBox(10);
        secantX0 = createTextField("2", "First initial guess");
        secantX1 = createTextField("3", "Second initial guess");
        paramsBox.getChildren().addAll(
                createLabeledField("x₀:", secantX0),
                createLabeledField("x₁:", secantX1)
        );
        methodParamsContainer.getChildren().add(paramsBox);
    }

    private TextField createTextField(String defaultValue, String prompt) {
        TextField textField = new TextField(defaultValue);
        textField.setPromptText(prompt);
        return textField;
    }

    private VBox createLabeledField(String label, TextField field) {
        VBox container = new VBox(5);
        Label fieldLabel = new Label(label);
        container.getChildren().addAll(fieldLabel, field);
        return container;
    }

    private void calculateRoots() {
        try {
            String equation = equationField.getText();
            double tolerance = Double.parseDouble(toleranceField.getText());
            int maxIterations = Integer.parseInt(maxIterationsField.getText());
            RootFindingMethod method = methodComboBox.getValue();

            NumericalMethods solver = new NumericalMethods(equation, tolerance, maxIterations);
            Map<RootFindingMethod, MethodResult> results = new HashMap<>();

            if (method == RootFindingMethod.ALL) {
                results.put(RootFindingMethod.BISECTION,
                        solver.bisection(Double.parseDouble(bisectionA.getText()),
                                Double.parseDouble(bisectionB.getText())));
                results.put(RootFindingMethod.FALSE_POSITION,
                        solver.falsePosition(Double.parseDouble(falsePositionA.getText()),
                                Double.parseDouble(falsePositionB.getText())));
                results.put(RootFindingMethod.FIXED_POINT,
                        solver.fixedPoint(Double.parseDouble(fixedPointX0.getText()),
                                fixedPointG.getText()));
                results.put(RootFindingMethod.NEWTON_RAPHSON,
                        solver.newtonRaphson(Double.parseDouble(newtonX0.getText())));
                results.put(RootFindingMethod.SECANT,
                        solver.secant(Double.parseDouble(secantX0.getText()),
                                Double.parseDouble(secantX1.getText())));

                displayComparison(results);

            } else {
                MethodResult result = calculateSingleMethod(solver, method);
                results.put(method, result);
                displaySingleMethodResult(result);
            }

            // -------------------------
            // 5. Plotting (stream version)
            // -------------------------
            List<Double> roots = results.values().stream()
                    .map(MethodResult::getRoot)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            plotFunction(equation, roots);
            // Switch to results tab
            resultsTabPane.getSelectionModel().select(resultsTab);

        } catch (Exception e) {
            showError("Calculation Error", e.getMessage());
        }
    }



    private MethodResult calculateSingleMethod(NumericalMethods solver, RootFindingMethod method) {
        switch (method) {
            case BISECTION:
                return solver.bisection(Double.parseDouble(bisectionA.getText()),
                        Double.parseDouble(bisectionB.getText()));
            case FALSE_POSITION:
                return solver.falsePosition(Double.parseDouble(falsePositionA.getText()),
                        Double.parseDouble(falsePositionB.getText()));
            case FIXED_POINT:
                return solver.fixedPoint(Double.parseDouble(fixedPointX0.getText()),
                        fixedPointG.getText());
            case NEWTON_RAPHSON:
                return solver.newtonRaphson(Double.parseDouble(newtonX0.getText()));
            case SECANT:
                return solver.secant(Double.parseDouble(secantX0.getText()),
                        Double.parseDouble(secantX1.getText()));
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }

    private void displaySingleMethodResult(MethodResult result) {
        resultsTable.getColumns().clear();
        resultsTable.getItems().clear();

        if (result.getErrorMessage() != null) {
            resultsSummary.setText("Error: " + result.getErrorMessage());
            return;
        }

        if (result.getRoot() != null) {
            resultsSummary.setText(String.format("Root found: %.8f (Converged in %d iterations)",
                    result.getRoot(), result.getIterations().size()));
        } else {
            resultsSummary.setText("Method did not converge within maximum iterations");
        }

        // Create table columns based on method type
        setupResultsTableColumns(result.getMethod());

        // Populate table
        ObservableList<IterationResult> items = FXCollections.observableArrayList(result.getIterations());
        resultsTable.setItems(items);
    }

    private void setupResultsTableColumns(RootFindingMethod method) {
        TableColumn<IterationResult, String> iterationCol = new TableColumn<>("Iteration");
        iterationCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getIteration())));
        resultsTable.getColumns().add(iterationCol);

        switch (method) {
            case BISECTION:
            case FALSE_POSITION:
                addColumn("a", "a", "%.6f");
                addColumn("b", "b", "%.6f");
                addColumn("Root", "root", "%.8f");
                addColumn("f(Root)", "fRoot", "%.4e");
                addColumn("Error %", "error", "%.6f");
                break;
            case FIXED_POINT:
                addColumn("x_i", "x_i", "%.8f");
                addColumn("x_i+1", "x_i1", "%.8f");
                addColumn("g(x_i)", "g_x", "%.8f");
                addColumn("Error %", "error", "%.6f");
                break;
            case NEWTON_RAPHSON:
                addColumn("x_i", "x_i", "%.8f");
                addColumn("x_i+1", "x_i1", "%.8f");
                addColumn("f(x_i)", "f_x", "%.4e");
                addColumn("f'(x_i)", "f_prime_x", "%.4e");
                addColumn("Error %", "error", "%.6f");
                break;
            case SECANT:
                addColumn("x_i-1", "x_i1", "%.8f");
                addColumn("x_i", "x_i", "%.8f");
                addColumn("f(x_i-1)", "f_x_i1", "%.4e");
                addColumn("f(x_i)", "f_x_i", "%.4e");
                addColumn("Error %", "error", "%.6f");
                break;
        }
    }

    private void addColumn(String header, String property, String format) {
        TableColumn<IterationResult, String> column = new TableColumn<>(header);
        column.setCellValueFactory(data -> {
            try {
                java.lang.reflect.Method getter = IterationResult.class.getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
                Object value = getter.invoke(data.getValue());
                if (value == null) return new SimpleStringProperty("-");
                if (value instanceof Double) {
                    return new SimpleStringProperty(String.format(format, (Double) value));
                }
                return new SimpleStringProperty(value.toString());
            } catch (Exception e) {
                return new SimpleStringProperty("-");
            }
        });
        resultsTable.getColumns().add(column);
    }

    private void displayComparison(Map<RootFindingMethod, MethodResult> results) {
        ObservableList<ComparisonData> comparisonData = FXCollections.observableArrayList();

        for (Map.Entry<RootFindingMethod, MethodResult> entry : results.entrySet()) {
            MethodResult result = entry.getValue();
            ComparisonData data = new ComparisonData();

            data.setMethod(entry.getKey().getDisplayName());
            data.setRoot(result.getRoot() != null ? String.format("%.8f", result.getRoot()) : "-");
            data.setIterations(String.valueOf(result.getIterations().size()));

            if (!result.getIterations().isEmpty()) {
                Double lastError = result.getIterations().get(result.getIterations().size() - 1).getError();
                data.setError(lastError != null ? String.format("%.6f", lastError) : "-");
            } else {
                data.setError("-");
            }

            if (result.getErrorMessage() != null) {
                data.setStatus("Error");
            } else if (result.isConverged()) {
                data.setStatus("Converged");
            } else {
                data.setStatus("No Convergence");
            }

            comparisonData.add(data);
        }

        comparisonTable.setItems(comparisonData);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}