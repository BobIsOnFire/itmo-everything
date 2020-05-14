package com.bobisonfire.lab4.jmx;

import com.bobisonfire.lab4.data.HistoryEntity;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.StringJoiner;

@ManagedResource
@Component
public class ClickPercentage implements ClickPercentageMBean {
    private double percentage;
    private int shootCounter;
    private int hitCounter;

    @ManagedAttribute
    public double getPercentage() {
        return percentage;
    }

    @Override
    public void addNode(HistoryEntity entity) {
        shootCounter++;
        if (entity.getResult() == 1) hitCounter++;
        percentage = hitCounter * 100.0 / shootCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClickPercentage that = (ClickPercentage) o;
        return Double.compare(that.percentage, percentage) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("percentage=" + percentage)
                .toString();
    }
}
