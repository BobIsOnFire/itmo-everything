package com.bobisonfire.parser;

import com.bobisonfire.function.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

class AdditiveParser {
    private boolean acceptSine = false;
    private boolean acceptCosine = false;
    private boolean acceptPower = false;
    private StringBuilder expression = new StringBuilder();
    private final List<Function> multipliers = new ArrayList<>();

    Function parse(String input) throws ParseException { // no + or - between root level expressions
        int bracketDepth = 0;

        String copy = input.trim();
        for (char ch : copy.toCharArray()) {
            if (bracketDepth > 0) expression.append(ch);

            if (ch == '(') {
                if (bracketDepth == 0) parseBareExpression();
                bracketDepth++;
                continue;
            }

            if (ch == ')') {
                bracketDepth--;
                if (bracketDepth == 0) {
                    Function function = new FunctionParser().parse(expression.toString());
                    multipliers.add(addModifiers(function));
                    expression = new StringBuilder();
                }
                continue;
            }

            if (bracketDepth > 0) continue;

            if (ch == '^') {
                parseBareExpression();
                acceptPower = true;
                continue;
            }

            if (ch == '*' || ch == ' ') {
                parseBareExpression();
                continue;
            }

            expression.append(ch);

            if (expression.toString().endsWith("sin")) {
                expression.delete(expression.length() - 3, expression.length());
                parseBareExpression();
                acceptSine = true;
                continue;
            }

            if (expression.toString().equals("cos")) {
                expression.delete(expression.length() - 3, expression.length());
                parseBareExpression();
                acceptCosine = true;
            }
        }

        parseBareExpression();
        return FunctionMul.from(multipliers.toArray(new Function[] {}));
    }

    // bare expression without signs and everything
    // should be number, variable or invalid
    private void parseBareExpression() throws ParseException {
        if (expression.length() > 0) {
            String trim = expression.toString().trim();
            expression = new StringBuilder();

            Function function;

            if (trim.matches("-?[0-9]+(\\.[0-9]+)?")) function = Constant.from(Double.parseDouble(trim));
            else if (trim.matches("([A-Za-zп][0-9]*)+")) function = parseVariableMul(trim);
            else if (trim.matches("-?[0-9]+(\\.[0-9]+)?([A-Za-zп][0-9]*)+")) {
                int letterIndex = trim.split("[A-Za-zп]")[0].length();
                function = FunctionMul.from(
                        Constant.from(Double.parseDouble(trim.substring(0, letterIndex))),
                        parseVariableMul(trim.substring(letterIndex))
                );
            }
            else throw new ParseException(trim, 0);

            function = addModifiers(function);
            multipliers.add(function);
        }
    }

    private Function addModifiers(Function function) throws ParseException {
        if (acceptSine || acceptCosine) function = TrigonometricFunction.from(function, acceptSine);
        if (acceptPower) {
            Function base = multipliers.remove(multipliers.size() - 1);
            if (base instanceof Constant) function = ExponentialFunction.from(base.getValue(0), function);
            else if (function instanceof Constant) function = PowerFunction.from(base, (int) function.getValue(0));
            else throw new ParseException(function.toString(), 0);
        }

        acceptPower = false;
        acceptSine = false;
        acceptCosine = false;

        return function;
    }

    private Function parseVariableMul(String input) {
        String replace = input.replaceAll("[A-Za-zп]", "@");
        int prevIndex = 0;
        int index;

        List<Function> variables = new ArrayList<>();
        while (true) {
            index = replace.indexOf('@', prevIndex + 1);
            if (index < 0) {
                variables.add(Variable.from(input.substring(prevIndex)));
                break;
            }
            variables.add(Variable.from(input.substring(prevIndex, index)));
            prevIndex = index;
        }

        return FunctionMul.from(variables.toArray(new Function[0]));
    }
}
