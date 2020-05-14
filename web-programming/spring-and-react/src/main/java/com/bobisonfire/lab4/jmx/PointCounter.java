package com.bobisonfire.lab4.jmx;

import com.bobisonfire.lab4.data.HistoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

@ManagedResource
@Component
public class PointCounter extends NotificationBroadcasterSupport implements PointCounterMBean {
    private int shootCounter;
    private int hitCounter;
    private int sequenceNumber = 1;

    @ManagedAttribute
    public int getShootCounter() {
        return shootCounter;
    }

    @ManagedAttribute
    public int getHitCounter() {
        return hitCounter;
    }

    public synchronized void addNode(HistoryEntity entity) {
        shootCounter++;
        if (entity.getResult() == 1) hitCounter++;

        BigDecimal abs = entity.getR().abs();
        BigDecimal x = entity.getX();
        BigDecimal y = entity.getY();

        if (x.compareTo(abs) > 0 || x.compareTo(abs.negate()) < 0 ||
                y.compareTo(abs) > 0 || y.compareTo(abs.negate()) < 0) {
            System.out.println("YO");
            Notification notification = new Notification(
                    "com.bobisonfire.lab4.outOfBounds", this, sequenceNumber++, System.currentTimeMillis(),
                    "Point is out of bounds: (" + x.toString() + "; " + y.toString() + ")"
            );

            this.sendNotification(notification);
        }
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[] {
                new MBeanNotificationInfo(
                        new String[] {"com.bobisonfire.lab4.outOfBounds"},
                        Notification.class.getName(),
                        "Sent when the point is out of bounds."
                )
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointCounter pointCounter = (PointCounter) o;
        return shootCounter == pointCounter.shootCounter &&
                hitCounter == pointCounter.hitCounter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shootCounter, hitCounter);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("shootCounter=" + shootCounter)
                .add("hitCounter=" + hitCounter)
                .toString();
    }
}
