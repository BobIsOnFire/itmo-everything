package com.bobisonfire.function;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class Point {
    private final Map<String, Double> variableMap = new HashMap<>();

    public static Point fromJSON(String json) {
        String[] tokens = json.replaceAll("[{}]", "").split("\\s*,\\s*");
        Point p = new Point();
        for (String t : tokens) {
            String[] elems = t.split("\\s*:\\s*");
            p.put(elems[0], Double.parseDouble(elems[1]));
        }
        return p;
    }

    public void put(String variable, Double value) {
        variableMap.put(variable, value);
    }

    public double get(String variable) {
        if (!variableMap.containsKey(variable)) return 0;
        return variableMap.get(variable);
    }

    public boolean isPrecise(Point other, double precision) {
        if (equals(other)) return true;
        for (String v : variableMap.keySet()) if (Math.abs(get(v) - other.get(v)) > precision) return false;
        return true;
    }

    public void incEach(double value) {
        for (String v : variableMap.keySet()) put(v, get(v) + value);
    }

    public Point copy() {
        Point p = new Point();
        for (String v : variableMap.keySet()) p.put(v, get(v));
        return p;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        for (String key : variableMap.keySet()) {
            joiner.add(key + " : " + get(key));
        }
        return "{" + joiner.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        for (String key : variableMap.keySet()) {
            if (get(key) != point.get(key)) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableMap);
    }
}
