package history;

import java.io.Serializable;
import java.math.BigDecimal;

public class HistoryNode implements Serializable {
    private String x;
    private String y;
    private String r;

    private boolean hit;

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getR() {
        return r;
    }

    public HistoryNode(String x, String y, String r) throws Exception {
        this.x = x;
        this.y = y;
        this.r = r;

        try {
            BigDecimal bigX = new BigDecimal(x);
            BigDecimal bigY = new BigDecimal(y);
            BigDecimal bigR = new BigDecimal(r);

            if ( bigX.abs().compareTo( BigDecimal.valueOf(4) ) > 0 ) // x > 4 OR x < -4
                throw new Exception("Некорректное значение X");
            if ( bigY.abs().compareTo( BigDecimal.valueOf(3) ) >= 0 ) // y >= 3 OR y <= -3
                throw new Exception("Некорректное значение Y");
            if ( bigR.subtract( BigDecimal.valueOf(2) ).abs().compareTo(BigDecimal.ONE) > 0) // r > 3 OR r < 1
                throw new Exception("Некорректное значение R");

            this.hit = calculateHit(bigX, bigY, bigR);

        } catch (NumberFormatException exc) {
            throw new Exception("Один из аргументов не является десятичным числом.");
        }
    }

    public boolean isHit() {
        return hit;
    }

    private boolean calculateHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        if ( x.compareTo(BigDecimal.ZERO) > 0 ) { // x > 0
            if ( y.compareTo(BigDecimal.ZERO) > 0 ) // y > 0
                return false;
            return x.compareTo(r) <= 0 && // x <= r
                    y.compareTo( r.negate().divide( BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP ) ) >= 0; // y >= -r / 2
        }

        if ( y.compareTo(BigDecimal.ZERO) > 0 ) // y >= 0
            return y.compareTo( x.add(r) ) <= 0; // y <= x + r
        return x.pow(2).add( y.pow(2) ).compareTo( r.pow(2).divide( BigDecimal.valueOf(4), BigDecimal.ROUND_HALF_UP ) ) <= 0; // x^2 + y^2 <= r^2 / 4
    }
}
