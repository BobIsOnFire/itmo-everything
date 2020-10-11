package com.bobisonfire;

import com.bobisonfire.function.*;

public class Mode {
    public static final int TRIGONOMETRIC = 0;
    public static final int EXPONENTIAL = 1;
    public static final int LINEAR = 2;
    public static final int CUSTOM = 3;

    private static final Function X = Variable.from("x");
    private static final Function Y = Variable.from("y");

    public static final Function COS_X = TrigonometricFunction.from(X, false);
    public static final Function MUL_XY = FunctionMul.from(Constant.from(2), X, Y);
    public static final Function CONSTANT = FunctionMul.from(Constant.from(3), PowerFunction.from(X, 3));

    public static final Function SIN_X = TrigonometricFunction.from(X, true);
    public static final Function EXP_SQUARE_X = ExponentialFunction.from(Math.E, PowerFunction.from(X, 2));
    public static final Function LINEAR_X = FunctionMul.from(Constant.from(0.75), PowerFunction.from(X, 4));
}
