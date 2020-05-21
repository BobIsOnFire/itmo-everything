package com.bobisonfire.parser;

import com.bobisonfire.function.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FunctionParser {
    public Function parse(String input) throws ParseException {
        int bracketDepth = 0;
        int additiveStartPointer = 0;

        List<Function> additives = new ArrayList<>();

        String copy = input.trim();
        boolean isNegative = copy.startsWith("-");
        if (isNegative) copy = copy.replace("-", "");

        int i = -1;
        for (char ch : copy.toCharArray()) {
            i++;
            if (ch == '(') {
                bracketDepth++;
                continue;
            }
            if (ch == ')') {
                bracketDepth--;
                continue;
            }

            if (bracketDepth == 0 && (ch == '+' || ch == '-')) {
                Function function = new AdditiveParser().parse(copy.substring(additiveStartPointer, i));
                if (isNegative) function = FunctionMul.from(Constant.from(-1), function);
                additives.add(function);

                additiveStartPointer = i + 1;
                isNegative = ch == '-';
            }
        }

        Function function = new AdditiveParser().parse(copy.substring(additiveStartPointer));
        if (isNegative) function = FunctionMul.from(Constant.from(-1), function);
        additives.add(function);

        return FunctionSum.from(additives.toArray(new Function[] {}));
    }
}
