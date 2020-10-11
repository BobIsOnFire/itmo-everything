package com.bobisonfire;

import com.bobisonfire.function.*;
import com.bobisonfire.parser.FunctionParser;
import com.bobisonfire.solver.AdamsSolver;
import com.bobisonfire.solver.NewtonInterpolator;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class Controller {
    private static final double FIELD_SIZE_DEFAULT = 10.0;
    private static final double CANVAS_SIZE = 400.0;
    private static final double PRECISION_MIN = 1E-4;
    private static final DecimalFormat df = new DecimalFormat("#");

    private static final CoordinateTranslator translateX = (v, center, size) -> CANVAS_SIZE * (v - center + size) / (2 * size);
    private static final CoordinateTranslator translateY = (v, center, size) -> CANVAS_SIZE * (center - v + size) / (2 * size);

    private static final CoordinateTranslator revertX = (c, center, size) -> 2 * size * c / CANVAS_SIZE + center - size;
    private static final CoordinateTranslator revertY = (c, center, size) -> center - 2 * size * c / CANVAS_SIZE + size;

    private static final String NUMBER_REGEXP = "-?[0-9]+(\\.[0-9]+)?([Ee]-?[0-9]+)?";

    public TextField firstEquationText;

    public RadioButton trigonometricButton;
    public RadioButton exponentialButton;
    public RadioButton linearButton;
    public RadioButton customButton;

    public TextField xStartText;
    public TextField yStartText;
    public TextField xEndText;
    public TextField precisionText;

    public TextField fieldSizeText;
    public TextField xCoordText;
    public TextField yCoordText;
    public Button drawEquationButton;

    public Label errorMessageLabel;

    public Button switchButton;
    public ScrollPane solutionPane;
    public Canvas canvas;
    public ToggleGroup equation;

    private boolean graphicsMode = false;
    private int equationMode = Mode.TRIGONOMETRIC;

    private boolean numbersValid = true;
    private boolean precisionValid = true;

    private boolean fieldSizeValid = true;
    private boolean centerValid = true;

    private Function resultFunction;
    private Function compareFunction;

    private final List<Double> basicPoints = new ArrayList<>();

    private boolean canvasDrag = false;
    private double prevX;
    private double prevY;
    private double velocityX = 0;
    private double velocityY = 0;

    public void initialize() {
        df.setMinimumIntegerDigits(1);
        df.setMaximumFractionDigits(5);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        xStartText.setText("0");
        yStartText.setText("0");
        xEndText.setText("1");
        precisionText.setText(df.format(PRECISION_MIN));

        precisionText.textProperty().addListener((ov, oldV, newV) -> {
            if (newV.isEmpty()) return;
            if (!newV.matches(NUMBER_REGEXP)) {
                errorMessageLabel.setText("Значение точности не является числом.");
                precisionValid = false;
                return;
            }

            double precision = Double.parseDouble(newV);
            if (precision >= PRECISION_MIN) {
                errorMessageLabel.setText(numbersValid ? "" : "Значение координат все еще не валидно.");
                precisionValid = true;
                return;
            }

            precisionValid = false;
            errorMessageLabel.setText("Значение точности должно быть больше 1E-4.");
        });

        ChangeListener<String> numberListener = (ov, oldV, newV) -> {
            String xText = xStartText.getText();
            String yText = yStartText.getText();
            String end = xEndText.getText();

            if (xText.isEmpty() || yText.isEmpty() || end.isEmpty()) return;
            if (!xText.matches(NUMBER_REGEXP) || !yText.matches(NUMBER_REGEXP) || !end.matches(NUMBER_REGEXP)) {
                errorMessageLabel.setText("Значение координат не является числом.");
                numbersValid = false;
                return;
            }

            if (Double.parseDouble(end) <= Double.parseDouble(xText)) {
                errorMessageLabel.setText("Координата конца отрезка должна быть больше координаты старта.");
                numbersValid = false;
                return;
            }

            errorMessageLabel.setText(precisionValid ? "" : "Значение точности все еще не валидно.");
            numbersValid = true;
        };

        xStartText.textProperty().addListener(numberListener);
        yStartText.textProperty().addListener(numberListener);
        xEndText.textProperty().addListener(numberListener);

        fieldSizeText.setText(df.format(FIELD_SIZE_DEFAULT));
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

        ChangeListener<String> coordListener = (ov, oldV, newV) -> {
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

        xCoordText.textProperty().addListener(coordListener);
        yCoordText.textProperty().addListener(coordListener);

        trigonometricButton.selectedProperty().addListener((obj, prevV, newV) -> {
            if (!prevV && newV) {
                equationMode = Mode.TRIGONOMETRIC;
                firstEquationText.setDisable(true);
            }
        });

        exponentialButton.selectedProperty().addListener((obj, prevV, newV) -> {
            if (!prevV && newV) {
                equationMode = Mode.EXPONENTIAL;
                firstEquationText.setDisable(true);
            }
        });

        linearButton.selectedProperty().addListener((obj, prevV, newV) -> {
            if (!prevV && newV) {
                equationMode = Mode.LINEAR;
                firstEquationText.setDisable(true);
            }
        });

        customButton.selectedProperty().addListener((obj, prevV, newV) -> {
            if (!prevV && newV) {
                equationMode = Mode.CUSTOM;
                firstEquationText.setDisable(false);
            }
        });
    }

    public void switchGraphicsMode() {
        graphicsMode = !graphicsMode;
        solutionPane.setVisible(!graphicsMode);
        switchButton.setText(graphicsMode ? "К корням" : "К графику");
        if (graphicsMode) repaint();
    }

    public void solve() {
        if (!numbersValid) {
            errorMessageLabel.setText("Значение координат все еще не валидно.");
            return;
        }

        if (!precisionValid) {
            errorMessageLabel.setText("Значение точности все еще не валидно.");
            return;
        }

        errorMessageLabel.setText("");

        try {
            Function equation;
            FunctionParser parser = new FunctionParser();

            switch (equationMode) {
                case Mode.TRIGONOMETRIC:
                    equation = Mode.COS_X;
                    break;
                case Mode.EXPONENTIAL:
                    equation = Mode.MUL_XY;
                    break;
                case Mode.LINEAR:
                    equation = Mode.CONSTANT;
                    break;
                default:
                    equation = parser.parse(firstEquationText.getText());
                    break;
            }

            double x0 = Double.parseDouble(xStartText.getText());
            double y0 = Double.parseDouble(yStartText.getText());
            Point start = new Point();
            start.put("x", x0);
            start.put("y", y0);

            double end = Double.parseDouble(xEndText.getText());
            double precision = Double.parseDouble(precisionText.getText());

            AdamsSolver solver = new AdamsSolver(equation, "x", "y");
            Point[] solution = solver.solve(start, end, precision);

            resultFunction = NewtonInterpolator.getInterpolationPolynom(solution, "x", "y");

            Function constant;
            switch (equationMode) {
                case Mode.TRIGONOMETRIC:
                    constant = Constant.from(y0 - Math.sin(x0));
                    compareFunction = FunctionSum.from(Mode.SIN_X, constant);
                    break;
                case Mode.EXPONENTIAL:
                    constant = Constant.from(y0 / Math.exp(x0 * x0));
                    compareFunction = FunctionMul.from(constant, Mode.EXP_SQUARE_X);
                    break;
                case Mode.LINEAR:
                    constant = Constant.from(y0 - 4 * x0);
                    compareFunction = FunctionSum.from(Mode.LINEAR_X, constant);
                    break;
                default:
                    compareFunction = null;
                    break;
            }

            basicPoints.clear();
            for (Point p : solution) basicPoints.add(p.get("x"));

            printResults(equation);
            if (graphicsMode) repaint();
            drawEquationButton.setDisable(false);
        } catch (ParseException e) {
            errorMessageLabel.setText("Ошибка парсинга функции.");
            drawEquationButton.setDisable(true);
            e.printStackTrace();
        }
    }

    private void printResults(Function equation) {
        FlowPane customPointPane = new FlowPane();
        customPointPane.setPrefWidth(385);
        List<Node> children = customPointPane.getChildren();
        Insets padding = new Insets(10);

        children.add(createLabel("Исходное уравнение:", 385, padding));
        children.add(createLabel("y' = " + equation.toString(), 385));

        if (equationMode != Mode.CUSTOM) {
            children.add(createLabel("Решение:", 385, padding));
            children.add(createLabel("y = " + compareFunction.toString(), 385));
        }

        children.add(createLabel("Таблица значений функции:", 385, padding));
        if (equationMode == Mode.CUSTOM) {
            children.add(createLabel("X", 192));
            children.add(createLabel("Y", 193));

            for (double x : basicPoints) {
                children.add(createLabel(x, 192));
                children.add(createLabel(resultFunction.getValue(x), 193));
            }
        } else {
            children.add(createLabel("X", 96));
            children.add(createLabel("Исходное", 96));
            children.add(createLabel("Рассчет", 96));
            children.add(createLabel("Дельта", 97));

            for (double x : basicPoints) {
                double basic = compareFunction.getValue(x);
                double count = resultFunction.getValue(x);
                double delta = Math.abs(basic - count);

                children.add(createLabel(x, 96));
                children.add(createLabel(basic, 96));
                children.add(createLabel(count, 96));
                children.add(createLabel(delta, 97));
            }
        }

        children.add(createLabel("Многочлен Ньютона:", 385, padding));
        Label polynom = createLabel("y = " + resultFunction.toString(), 385);
        polynom.setWrapText(true);
        children.add(polynom);

        solutionPane.setContent(customPointPane);
    }

    private Label createLabel(double value, double width) {
        return createLabel(df.format(value), width);
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

    public void repaint() {
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
        gc.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

        drawField(gc, centerX, centerY, size);
        if (resultFunction != null) drawFunction(gc, resultFunction, Color.BLUE, centerX, centerY, size);
        if (compareFunction != null) drawFunction(gc, compareFunction, Color.RED, centerX, centerY, size);

        for (double point : basicPoints) {
            drawDot(gc,
                    translateX.translate(point, centerX, size),
                    translateY.translate(resultFunction.getValue(point), centerY, size),
                    Color.GREEN
            );

            if (equationMode != Mode.CUSTOM) {
                drawDot(gc,
                        translateX.translate(point, centerX, size),
                        translateY.translate(compareFunction.getValue(point), centerY, size),
                        Color.MAGENTA
                );
            }
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

    private void drawDot(GraphicsContext gc, double x, double y, Color color) {
        gc.setFill(color);
        if (y >= 0 && y <= CANVAS_SIZE) {
            gc.fillOval(x - 4, y - 4, 8, 8);
            return;
        }

        gc.beginPath();
        if (y < 0) {
            gc.moveTo(x, 0);
            gc.lineTo(x - 8, 8);
            gc.lineTo(x - 4, 8);
            gc.lineTo(x - 4, 16);
            gc.lineTo(x + 4, 16);
            gc.lineTo(x + 4, 8);
            gc.lineTo(x + 8, 8);
            gc.lineTo(x, 0);
        } else {
            gc.moveTo(x, CANVAS_SIZE);
            gc.lineTo(x - 8, CANVAS_SIZE - 8);
            gc.lineTo(x - 4, CANVAS_SIZE - 8);
            gc.lineTo(x - 4, CANVAS_SIZE - 16);
            gc.lineTo(x + 4, CANVAS_SIZE - 16);
            gc.lineTo(x + 4, CANVAS_SIZE - 8);
            gc.lineTo(x + 8, CANVAS_SIZE - 8);
            gc.lineTo(x, CANVAS_SIZE);
        }
        gc.fill();
    }

    private void drawField(GraphicsContext gc, double centerX, double centerY, double size) {
        df.setMaximumFractionDigits(2);

        double arrowX = translateX.translate(0, centerX, size);
        double arrowY = translateY.translate(0, centerY, size);
        double step = CANVAS_SIZE / 10;

        gc.setStroke(Color.GRAY);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(1);
        gc.setFont(Font.font(10));

        gc.beginPath();

        for (double i = arrowX % step; i <= CANVAS_SIZE; i += step) {
            gc.moveTo(i, 0);
            gc.lineTo(i, CANVAS_SIZE);
            gc.fillText(df.format(revertX.translate(i, centerX, size)), i + 2, CANVAS_SIZE - 3);
        }

        for (double i = arrowY % step; i <= CANVAS_SIZE; i += step) {
            gc.moveTo(0, i);
            gc.lineTo(CANVAS_SIZE, i);
            gc.fillText(df.format(revertY.translate(i, centerY, size)), 2, i + 12);
        }

        gc.stroke();

        if (arrowX - step < 0 || arrowX + step > CANVAS_SIZE) arrowX = 0;
        if (arrowY - step < 0 || arrowY + step > CANVAS_SIZE) arrowY = CANVAS_SIZE;

        double arrowHeight = step / 2;
        double arrowWidth = arrowHeight / 3;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.beginPath();

        gc.moveTo(arrowX, CANVAS_SIZE);
        gc.lineTo(arrowX, 0);
        gc.lineTo(arrowX - arrowWidth, arrowHeight);
        gc.moveTo(arrowX, 0);
        gc.lineTo(arrowX + arrowWidth, arrowHeight);

        gc.moveTo(0, arrowY);
        gc.lineTo(CANVAS_SIZE, arrowY);
        gc.lineTo(CANVAS_SIZE - arrowHeight, arrowY - arrowWidth);
        gc.moveTo(CANVAS_SIZE, arrowY);
        gc.lineTo(CANVAS_SIZE - arrowHeight, arrowY + arrowWidth);

        gc.stroke();

        gc.setLineWidth(1);
        gc.setFont(Font.font(arrowHeight));

        if (arrowX > arrowWidth * 3) gc.fillText("y", arrowX - arrowWidth * 3, arrowHeight * 2);
        else gc.fillText("y", arrowX + arrowWidth, arrowHeight * 2);

        if (arrowY > arrowWidth * 3) gc.fillText("x", CANVAS_SIZE - arrowHeight * 2, arrowY - arrowWidth);
        else gc.fillText("x", CANVAS_SIZE - arrowHeight * 2, arrowY + arrowWidth * 3);

        df.setMaximumFractionDigits(5);
    }

    public void mouseRelease() {
        if (!canvasDrag) return;
        canvasDrag = false;

        PauseTransition decay = new PauseTransition(Duration.millis(1000.0 / 60));
        decay.setOnFinished(e -> {
            velocityX *= 0.96;
            if (Math.abs(velocityX) <= 1) velocityX = 0;
            velocityY *= 0.96;
            if (Math.abs(velocityY) <= 1) velocityY = 0;

            moveTo(prevX + velocityX, prevY + velocityY);

            if (!canvasDrag && (velocityX != 0 || velocityY != 0)) decay.playFromStart();
        });
        decay.play();
    }

    public void mouseMove(MouseEvent mouseEvent) {
        if (!canvasDrag) return;
        moveTo(mouseEvent.getScreenX(), mouseEvent.getScreenY());
    }

    public void startMove(MouseEvent mouseEvent) {
        canvasDrag = true;
        prevX = mouseEvent.getScreenX();
        prevY = mouseEvent.getScreenY();
    }

    public void zoomCanvas(ScrollEvent scrollEvent) {
        double delta = Math.abs(scrollEvent.getDeltaY()) / 100.0 + 1;
        if (delta > 2) delta = 2;
        if (scrollEvent.getDeltaY() > 0) delta = 1 / delta;

        double size = Double.parseDouble(fieldSizeText.getText()) * delta;
        if (size < 1E-5) size = 1E-5;

        fieldSizeText.setText(df.format(size));

        repaint();
    }

    private void moveTo(double newX, double newY) {
        velocityX = newX - prevX;
        velocityY = newY - prevY;

        double size = Double.parseDouble(fieldSizeText.getText());
        double x = Double.parseDouble(xCoordText.getText()) - velocityX * size / 200.0;
        double y = Double.parseDouble(yCoordText.getText()) + velocityY * size / 200.0;

        xCoordText.setText(df.format(x));
        yCoordText.setText(df.format(y));

        prevX = newX;
        prevY = newY;

        repaint();
    }
}
