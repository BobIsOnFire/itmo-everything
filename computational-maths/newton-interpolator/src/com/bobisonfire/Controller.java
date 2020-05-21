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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Controller {
    private static final double FIELD_SIZE_DEFAULT = 10.0;
    private static final double CANVAS_SIZE = 400.0;
    private static final double OUTPUT_PRECISION = 1E-5;

    private static final CoordinateTranslator translateX = (v, center, size) -> CANVAS_SIZE * (v - center + size) / (2 * size);
    private static final CoordinateTranslator translateY = (v, center, size) -> CANVAS_SIZE * (center - v + size) / (2 * size);

    private static final String NUMBER_REGEXP = "-?[0-9]+(\\.[0-9]+)?([Ee]-?[0-9]+)?";

    public TextField firstEquationText;
    public FlowPane pointsPane;

    public TextField fieldSizeText;
    public TextField xCoordText;
    public TextField yCoordText;
    public Button drawEquationButton;

    public Label errorMessageLabel;

    public Button switchButton;
    public ScrollPane solutionPane;
    public Canvas canvas;

    private boolean graphicsMode = false;

    private boolean fieldSizeValid = true;
    private boolean centerValid = true;

    private Function basicFunction;
    private Function interpolatedFunction;

    private final List<Double> basicPoints = new ArrayList<>();
    private final List<Double> customPoints = new ArrayList<>();

    public void initialize() {
        fieldSizeText.setText(String.valueOf(FIELD_SIZE_DEFAULT));
        xCoordText.setText("0");
        yCoordText.setText("0");

        fieldSizeText.textProperty().addListener((ov, oldV, newV) -> {
            if (newV.isEmpty()) return;
            if (!newV.matches(NUMBER_REGEXP)) {
                errorMessageLabel.setText("Значение размера поля не является числом.");
                fieldSizeValid = false;
                return;
            }

            double size = Double.parseDouble(newV);
            if (size > 0) {
                errorMessageLabel.setText(centerValid ? "" : "Значение границ все еще не валидно.");
                fieldSizeValid = true;
                return;
            }

            fieldSizeValid = false;
            errorMessageLabel.setText("Значение размера поля должно быть положительным.");
        });

        ChangeListener<String> listener = (ov, oldV, newV) -> {
            String xText = xCoordText.getText();
            String yText = yCoordText.getText();

            if (xText.isEmpty() || yText.isEmpty()) return;
            if (!xText.matches(NUMBER_REGEXP) || !yText.matches(NUMBER_REGEXP)) {
                errorMessageLabel.setText("Значение координаты центра не является числом.");
                centerValid = false;
                return;
            }

            errorMessageLabel.setText(fieldSizeValid ? "" : "Значение размера поля все еще не валидно.");
            centerValid = true;
        };

        xCoordText.textProperty().addListener(listener);
        yCoordText.textProperty().addListener(listener);

    }

    public void addPointButton() {
        List<Node> children = pointsPane.getChildren();
        TextField x = new TextField("0");
        x.setPrefWidth(pointsPane.getPrefWidth() / 2);
        children.add(x);
    }

    public void removePointButton() {
        List<Node> children = pointsPane.getChildren();
        if (children.isEmpty()) return;
        children.remove(children.size() - 1);
    }

    public void switchGraphicsMode() {
        graphicsMode = !graphicsMode;
        solutionPane.setVisible(!graphicsMode);
        switchButton.setText(graphicsMode ? "К корням" : "К графику");
        if (graphicsMode) repaint();
    }

    public void interpolate() {
        List<Node> children = pointsPane.getChildren();
        if (children.isEmpty()) {
            errorMessageLabel.setText("Для выполнения необходима хотя бы одна опорная точка.");
            drawEquationButton.setDisable(true);
            return;
        }

        for (Node n : children) {
            String text = ((TextField) n).getText();
            if (!text.matches(NUMBER_REGEXP)) {
                errorMessageLabel.setText("Одна из опорных точек не является числом: " + text);
                drawEquationButton.setDisable(true);
                return;
            }
        }

        errorMessageLabel.setText("");

        try {
            basicFunction = new FunctionParser().parse(firstEquationText.getText());
            String x = Variable.variableNames()[0];
            Variable.clearPool();

            if (x.equalsIgnoreCase("y")) {
                errorMessageLabel.setText("Используйте для неизвестной в функции другую переменную.");
                drawEquationButton.setDisable(true);
                return;
            }

            basicPoints.clear();
            customPoints.clear();

            Point[] points = new Point[children.size()];
            int k = 0;
            for (Node n : children) {
                Point p = new Point();
                double value = Double.parseDouble(((TextField) n).getText());
                p.put(x, value);
                p.put("y", basicFunction.getValue(value));
                points[k++] = p;

                basicPoints.add(value);
            }

            interpolatedFunction = NewtonInterpolator.getInterpolationPolynom(points, x, "y");
            printResults();
            if (graphicsMode) repaint();
            drawEquationButton.setDisable(false);
        } catch (ParseException e) {
            errorMessageLabel.setText("Ошибка парсинга функции.");
            drawEquationButton.setDisable(true);
            e.printStackTrace();
        }
    }

    private void printResults() {
        FlowPane pane = new FlowPane();
        pane.setPrefWidth(385);
        List<Node> children = pane.getChildren();
        Insets padding = new Insets(10);

        children.add(createLabel("Исходная функция:", 385, padding));
        children.add(createLabel("y = " + basicFunction.toString(), 385));

        children.add(createLabel("Многочлен Ньютона:", 385, padding));
        Label polynom = createLabel("y = " + interpolatedFunction.toString(), 385);
        polynom.setWrapText(true);
        children.add(polynom);

        children.add(createLabel("Пользовательские точки", 385, padding));
        TextField custom = new TextField("0");
        custom.setPrefWidth(250);
        Button check = new Button("Найти значение");
        check.setPrefWidth(135);

        children.add(custom);
        children.add(check);

        children.add(createLabel("Аргумент", 96, padding));
        children.add(createLabel("Исходное", 96, padding));
        children.add(createLabel("Многочлен", 96, padding));
        children.add(createLabel("Дельта", 97, padding));

        custom.textProperty().addListener((ov, oldV, newV) -> {
            if (newV.isEmpty()) {
                check.setDisable(true);
                return;
            }
            if (!newV.matches(NUMBER_REGEXP)) {
                errorMessageLabel.setText("Значение пользовательской точки не является числом.");
                check.setDisable(true);
                return;
            }
            errorMessageLabel.setText("");
            check.setDisable(false);
        });

        check.setOnMouseClicked(e -> {
            if (check.isDisable()) return;

            double value = Double.parseDouble(custom.getText());
            double basic = basicFunction.getValue(value);
            double interpolated = interpolatedFunction.getValue(value);

            children.add(createLabel(value, 96));
            children.add(createLabel(basic, 96));
            children.add(createLabel(interpolated, 96));
            children.add(createLabel(Math.abs(basic - interpolated), 97));

            customPoints.add(value);
        });

        solutionPane.setContent(pane);
    }

    private Label createLabel(double value, double width) {
        return createLabel(format(value), width);
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

    public void repaint() { // todo drag'n'drop and scroll support
        if (!fieldSizeValid) {
            errorMessageLabel.setText("Значение размера поля все еще не валидно.");
            return;
        }

        if (!centerValid) {
            errorMessageLabel.setText("Значение координат центра все еще не валидно.");
            return;
        }

        double size = Double.parseDouble(fieldSizeText.getText());
        double centerX = Double.parseDouble(xCoordText.getText());
        double centerY = Double.parseDouble(yCoordText.getText());

        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawField(gc, translateX.translate(0, centerX, size), translateY.translate(0, centerY, size));
        drawFunction(gc, basicFunction, Color.BLUE, centerX, centerY, size);
        drawFunction(gc, interpolatedFunction, Color.RED, centerX, centerY, size);

        for (double point : basicPoints) {
            drawDot(gc,
                    translateX.translate(point, centerX, size),
                    translateY.translate(basicFunction.getValue(point), centerY, size),
                    Color.BLACK
            );
        }

        for (double point : customPoints) {
            drawDot(gc,
                    translateX.translate(point, centerX, size),
                    translateY.translate(basicFunction.getValue(point), centerY, size),
                    Color.GREEN
            );
            drawDot(gc,
                    translateX.translate(point, centerX, size),
                    translateY.translate(interpolatedFunction.getValue(point), centerY, size),
                    Color.YELLOW
            );
        }
    }

    private void drawFunction(GraphicsContext gc, Function function, Color color, double centerX, double centerY, double size) {
        Predicate<Double> inBounds = x -> x > centerY - size && x < centerY + size;

        gc.setStroke(color);
        gc.setLineWidth(2);

        double step = (2 * size) / CANVAS_SIZE;
        double start = function.getValue(centerX - size);
        gc.beginPath();
        gc.moveTo(0, translateY.translate(start, centerY, size));
        double prevValue = start;

        for (double x = centerX - size + step; x < centerX + size; x += step) {
            double value = function.getValue(x);
            if (inBounds.test(prevValue) || inBounds.test(value)) {
                gc.lineTo(
                        translateX.translate(x, centerX, size),
                        translateY.translate(value, centerY, size)
                );
            } else {
                gc.moveTo(
                        translateX.translate(x, centerX, size),
                        value > centerY + size ? 0 : CANVAS_SIZE
                );
            }

            prevValue = value;
        }

        gc.stroke();

    }

    private void drawDot(GraphicsContext gc, double x, double y, Color color) { // todo arrow if a point is above or below the field
        gc.setFill(color);
        gc.fillOval(x - 4, y - 4, 8, 8);
    }

    private void drawField(GraphicsContext gc, double arrowX, double arrowY) { // todo axes and numbers!
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

    private String format(double value) {
        return String.valueOf(Math.round(value / OUTPUT_PRECISION) * OUTPUT_PRECISION);
    }
}
