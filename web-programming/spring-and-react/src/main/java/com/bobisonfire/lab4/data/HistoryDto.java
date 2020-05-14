package com.bobisonfire.lab4.data;

import com.bobisonfire.lab4.validation.BigDecimalConstraint;

import javax.validation.constraints.NotNull;

public class HistoryDto {
    @NotNull(message = "Значение X не задано.")
    @BigDecimalConstraint(min = -3.0d, max = 5.0d, message = "X не принадлежит заданному интервалу.")
    private String x;

    @NotNull(message = "Значение Y не задано.")
    @BigDecimalConstraint(
            min = -3.0d, minIncluding = false,
            max = 3.0d, maxIncluding = false,
            message = "Y не принадлежит заданному интервалу."
    )
    private String y;

    @NotNull(message = "Значение R не задано.")
    @BigDecimalConstraint(min = -3.0d, max = 5.0d, message = "R не принадлежит заданному интервалу.")
    private String r;

    private boolean clicked;

    private boolean result;

    public HistoryDto() {
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
