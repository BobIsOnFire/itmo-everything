package com.bobisonfire;

import com.bobisonfire.function.Function;
import com.bobisonfire.function.Point;
import com.bobisonfire.function.Variable;
import com.bobisonfire.parser.FunctionParser;
import com.bobisonfire.solver.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

import java.text.ParseException;
import java.util.List;
import java.util.function.Predicate;

public class Controller {
    private static final double PRECISION_MIN = 1E-8;
    private static final double PRECISION_DEFAULT = 1E-4;
    private static final double LEFT_BORDER_DEFAULT = -50.0;
    private static final double RIGHT_BORDER_DEFAULT = 50.0;

    private static final int MAX_STEPS = 100;
    private static final double STEP_SIZE = 0.25;

    private static final double CANVAS_SIZE = 400.0;
    private static final CoordinateTranslator translateX = (v, from, to) -> CANVAS_SIZE * (v - from) / (to - from);
    private static final CoordinateTranslator translateY = (v, from, to) -> CANVAS_SIZE * (to - v) / (to - from);


    private static final String NUMBER_REGEXP = "-?[0-9]+(\\.[0-9]+)?([Ee]-?[0-9]+)?";

    public TextField firstEquationText;
    public ToggleGroup method;
    public RadioButton halfSolverButton;
    public RadioButton iterateSolverButton;
    public TextField secondEquationText;

    public FlowPane oneEquationPane;
    public FlowPane twoEquationPane;

    public TextField precisionText;
    public TextField leftBorderText;
    public TextField rightBorderText;

    public Slider stepSizeSlider;
    public Label stepSizeLabel;

    public Label errorMessageLabel;

    public Button switchButton;
    public ScrollPane solutionPane;
    public Canvas canvas;

    private boolean systemModeFlag = false;
    private boolean graphicsMode = false;

    private boolean precisionValid = true;
    private boolean bordersValid = true;

    public void initialize() {
        precisionText.setText(String.valueOf(PRECISION_DEFAULT));
        leftBorderText.setText(String.valueOf(LEFT_BORDER_DEFAULT));
        rightBorderText.setText(String.valueOf(RIGHT_BORDER_DEFAULT));

        int max = (int) (RIGHT_BORDER_DEFAULT - LEFT_BORDER_DEFAULT);
        int min = max / MAX_STEPS;
        stepSizeSlider.setMin(min);
        stepSizeSlider.setMax(max);
        stepSizeSlider.setValue(min);

        stepSizeSlider.valueProperty().addListener((obs, oldV, newV) ->
                stepSizeSlider.setValue(Math.round(newV.doubleValue() / STEP_SIZE) * STEP_SIZE));

        precisionText.textProperty().addListener((ov, oldV, newV) -> {
            if (newV.isEmpty()) return;
            if (!newV.matches(NUMBER_REGEXP)) {
                errorMessageLabel.setText("Значение точности не является числом.");
                precisionValid = false;
                return;
            }

            double precision = Double.parseDouble(newV);
            if (precision >= PRECISION_MIN) {
                errorMessageLabel.setText(bordersValid ? "" : "Значение границ все еще не валидно.");
                precisionValid = true;
                return;
            }

            precisionValid = false;
            errorMessageLabel.setText(precision <= 0 ?
                    "Значение точности должно быть положительным." :
                    "Значение точности слишком мало."
            );
        });

        ChangeListener<String> listener = (ov, oldV, newV) -> {
            String leftText = leftBorderText.getText();
            String rightText = rightBorderText.getText();

            if (leftText.isEmpty() || rightText.isEmpty()) return;
            if (!leftText.matches(NUMBER_REGEXP) || !rightText.matches(NUMBER_REGEXP)) {
                errorMessageLabel.setText("Значение границы не является числом.");
                bordersValid = false;
                return;
            }

            double left = Double.parseDouble(leftText);
            double right = Double.parseDouble(rightText);

            if (right <= left) {
                errorMessageLabel.setText("Правая граница должна быть больше левой.");
                bordersValid = false;
                return;
            }

            errorMessageLabel.setText(precisionValid ? "" : "Значение точности все еще не валидно.");
            bordersValid = true;

            double vMax = right - left;
            double vMin = vMax / MAX_STEPS;
            if (vMin < STEP_SIZE) vMin = STEP_SIZE;

            double value = stepSizeSlider.getValue();
            if (value < vMin || value > vMax) value = vMin;

            stepSizeSlider.setMin(vMin);
            stepSizeSlider.setMax(vMax);
            stepSizeSlider.setValue(value);
        };

        leftBorderText.textProperty().addListener(listener);
        rightBorderText.textProperty().addListener(listener);

    }

    public void addEquation() {
        if (systemModeFlag) return;

        systemModeFlag = true;
        oneEquationPane.setVisible(false);
        FlowPane.setMargin(oneEquationPane, new Insets(-25, 0, 0, 0));
        twoEquationPane.setVisible(true);
        FlowPane.setMargin(twoEquationPane, new Insets(0));
    }

    public void deleteEquation() {
        if (!systemModeFlag) return;

        systemModeFlag = false;
        oneEquationPane.setVisible(true);
        FlowPane.setMargin(oneEquationPane, new Insets(0));
        twoEquationPane.setVisible(false);
        FlowPane.setMargin(twoEquationPane, new Insets(-25, 0, 0, 0));
    }

    public void switchGraphicsMode() {
        graphicsMode = !graphicsMode;
        solutionPane.setVisible(!graphicsMode);
        switchButton.setText(graphicsMode ? "К корням" : "К графику");
    }

    public void solveEquations() {
        if (!precisionValid) {
            errorMessageLabel.setText("Значение точности все еще не валидно.");
            return;
        }

        if (!bordersValid) {
            errorMessageLabel.setText("Значение границ все еще не валидно.");
            return;
        }

        errorMessageLabel.setText("");

        double left = Double.parseDouble(leftBorderText.getText());
        double right = Double.parseDouble(rightBorderText.getText());
        double precision = Double.parseDouble(precisionText.getText());
        double stepSize = stepSizeSlider.getValue();

        try {
            if (systemModeFlag) {
                Function f1 = new FunctionParser().parse(firstEquationText.getText());
                Function f2 = new FunctionParser().parse(secondEquationText.getText());
                NewtonSystemSolver solver = new NewtonSystemSolver(f1, f2);
                SystemSolution solution = solver.getSolution(left, right, precision, stepSize);
                printSolution(solution);
                drawSolution(solution, left, right);
            } else {
                Function f = new FunctionParser().parse(firstEquationText.getText());
                EquationSolver solver;

                RadioButton chk = (RadioButton) method.getSelectedToggle();
                if (chk.getText().equals(halfSolverButton.getText())) solver = new HalfSolver(f);
                else if (chk.getText().equals(iterateSolverButton.getText())) solver = new IterateSolver(f);
                else {
                    compareSolutions(f, left, right, precision, stepSize);
                    return;
                }

                Solution solution = solver.getSolution(left, right, precision, stepSize);
                printSolution(solution);
                drawSolution(solution, left, right);
            }
        } catch (ParseException exc) {
            errorMessageLabel.setText("Ошибка парсинга функций: " + exc.getMessage());
        } catch (SolverException exc) {
            errorMessageLabel.setText("Ошибка решения уравнения: " + exc.getMessage());
        }
    }

    private void compareSolutions(Function f, double left, double right, double precision, double step) {
        Solution s1 = new HalfSolver(f).getSolution(left, right, precision, step);
        Function temp = Variable.from("x");
        Solution s2 = new IterateSolver(f).getSolution(left, right, precision, step);
        int width = 385 / 3;

        FlowPane pane = new FlowPane();
        pane.setPrefWidth(385);
        List<Node> children = pane.getChildren();
        Insets padding = new Insets(10);

        children.add(createLabel("Уравнение:", 385, padding));
        children.add(createLabel(f.toString() + " = 0", 385));

        double[] roots1 = s1.getRoots();
        double[] roots2 = s2.getRoots();

        if (roots1.length == 0 || roots2.length == 0) {
            children.add(createLabel("Решений нет", 385, padding));
        } else {
            children.add(createLabel("Решения:", 385, padding));
            children.add(createLabel("Пополам", 120));
            children.add(createLabel("Итерации", 120));
            children.add(createLabel("Дельта", 135));

            for (int i = 0; i < Math.min(roots1.length, roots2.length); i++) {
                children.add(createLabel(String.valueOf(roots1[i]), 120));
                children.add(createLabel(String.valueOf(roots2[i]), 120));
                children.add(createLabel(String.valueOf( Math.abs(roots1[i] - roots2[i]) ), 135));
                System.out.println(Math.abs(roots1[i] - roots2[i]));
            }
        }

        solutionPane.setContent(pane);
        drawSolution(s1, left, right);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        for (double root : roots2) {
            gc.fillOval(translateX.translate(root, left, right) - 4, translateY.translate(0, left, right) - 4, 8, 8);
        }

    }

    private void printSolution(Solution solution) {
        FlowPane pane = new FlowPane();
        pane.setPrefWidth(385);
        List<Node> children = pane.getChildren();
        Insets padding = new Insets(10);

        children.add(createLabel("Уравнение:", 385, padding));
        children.add(createLabel(solution.getFunction().toString() + " = 0", 385));

        double[] roots = solution.getRoots();
        if (roots.length == 0) {
            children.add(createLabel("Решений нет", 385, padding));
        } else {
            children.add(createLabel("Решения:", 385, padding));
            for (double r : roots) {
                children.add(createLabel(String.valueOf(r), 385));
            }
        }

        solutionPane.setContent(pane);
    }

    private void printSolution(SystemSolution solution) {
        FlowPane pane = new FlowPane();
        pane.setPrefWidth(385);
        List<Node> children = pane.getChildren();
        Insets padding = new Insets(10);

        Function[] functions = solution.getFunctions();
        String[] variables = solution.getVariables();
        Point[] roots = solution.getRoots();
        int width = 385 / functions.length;

        children.add(createLabel("Уравнения:", 385, padding));
        for (Function f : functions) children.add(createLabel(f.toString() + " = 0", 385));

        if (roots.length == 0) {
            children.add(createLabel("Решений нет", 385, padding));
        } else {
            children.add(createLabel("Решения:", 385, padding));
            for (String v : variables) children.add(createLabel(v, width));
            for (Point root : roots) {
                for (String v : variables)
                    children.add(createLabel(String.valueOf(root.get(v)), width));
            }
        }

        solutionPane.setContent(pane);
    }

    private Label createLabel(String text, double width) {
        return createLabel(text, width, new Insets(0));
    }

    private Label createLabel(String text, double width, Insets padding) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setAlignment(Pos.CENTER);
        label.setPadding(padding);
        return label;
    }

    private void drawSolution(Solution solution, double from, double to) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawField(gc, translateX.translate(0, from, to), translateY.translate(0, from, to));

        Function function = solution.getFunction();
        Predicate<Double> inBounds = x -> x > from && x < to;

        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

        double step = (to - from) / CANVAS_SIZE;
        double start = function.getValue(from);
        gc.beginPath();
        gc.moveTo(0, translateY.translate(start, from, to));
        double prevValue = start;

        for (double x = from + step; x < to; x += step) {
            double value = function.getValue(x);
            if (inBounds.test(prevValue) || inBounds.test(value)) {
                gc.lineTo(
                        translateX.translate(x, from, to),
                        translateY.translate(value, from, to)
                );
            } else {
                gc.moveTo(
                        translateX.translate(x, from, to),
                        value > to ? 0 : CANVAS_SIZE
                );
            }

            prevValue = value;
        }

        gc.stroke();

        double[] roots = solution.getRoots();
        gc.setFill(Color.YELLOWGREEN);
        for (double root : roots) {
            gc.fillOval(translateX.translate(root, from, to) - 4, translateY.translate(0, from, to) - 4, 8, 8);
        }
    }

    private void drawSolution(SystemSolution solution, double from, double to) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawField(gc, translateX.translate(0, from, to), translateY.translate(0, from, to));

        Function[] functions = solution.getFunctions();
        String[] variables = solution.getVariables();
        String varX;
        String varY;
        if (variables[1].equalsIgnoreCase("x")) {
            varX = variables[1];
            varY = variables[0];
        } else {
            varX = variables[0];
            varY = variables[1];
        }

        drawEquation(gc, functions[0], varX, varY, from, to, Color.BLUE);
        drawEquation(gc, functions[1], varX, varY, from, to, Color.RED);

        Point[] roots = solution.getRoots();
        gc.setFill(Color.YELLOWGREEN);
        for (Point root : roots) {
            double x = CANVAS_SIZE * (root.get(varX) - from) / (to - from);
            double y = CANVAS_SIZE * (to - root.get(varY)) / (to - from);
            gc.fillOval(x - 4, y - 4, 8, 8);
        }
    }

    private void drawField(GraphicsContext gc, double arrowX, double arrowY) {
        gc.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);

        double step = CANVAS_SIZE / 10;

        gc.beginPath();
        for (double i = arrowX % step; i <= CANVAS_SIZE; i += step) {
            gc.moveTo(i, 0);
            gc.lineTo(i, CANVAS_SIZE);
        }

        for (double i = arrowY % step; i <= CANVAS_SIZE; i += step) {
            gc.moveTo(0, i);
            gc.lineTo(CANVAS_SIZE, i);
        }

        gc.stroke();

        double arrowHeight = step / 2;
        double arrowWidth = arrowHeight / 3;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.beginPath();

        if (arrowX >= 0 && arrowX <= CANVAS_SIZE) {
            gc.moveTo(arrowX, CANVAS_SIZE);
            gc.lineTo(arrowX, 0);
            gc.lineTo(arrowX - arrowWidth, arrowHeight);
            gc.moveTo(arrowX, 0);
            gc.lineTo(arrowX + arrowWidth, arrowHeight);
        }

        if (arrowY >= 0 && arrowY <= CANVAS_SIZE) {
            gc.moveTo(0, arrowY);
            gc.lineTo(CANVAS_SIZE, arrowY);
            gc.lineTo(CANVAS_SIZE - arrowHeight, arrowY - arrowWidth);
            gc.moveTo(CANVAS_SIZE, arrowY);
            gc.lineTo(CANVAS_SIZE - arrowHeight, arrowY + arrowWidth);
        }

        gc.stroke();
    }

    private void drawEquation(GraphicsContext gc, Function function, String varX, String varY, double from, double to, Color color) {
        double step = (to - from) / CANVAS_SIZE;
        double precision = (to - from) / 300;

        gc.setFill(color);
        Point p = new Point();
        for (double x = from; x < to; x += step) {
            p.put(varX, x);
            for (double y = from; y < to; y += step) {
                p.put(varY, y);
                if (Math.abs(function.getValue(p)) <= precision)
                    gc.fillOval(
                            translateX.translate(x, from, to) - 1,
                            translateY.translate(y, from, to) - 1,
                            2, 2
                    );
            }
        }
    }
}
