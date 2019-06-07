package com.bobisonfire.foodshell.guiframe;

import javax.swing.*;
import java.awt.*;

class MainTable extends JPanel {
    private static final int ROWS_COUNT = 15;

    private class Cell extends JLabel {
        Cell(String label) {
            super(label, SwingConstants.CENTER);
            this.setBorder( BorderFactory.createLineBorder(Color.BLACK) );
            this.setFont(this.getFont().deriveFont(14.0f));
        }
    }

    private class Row extends JPanel {
        static final int LAST = 1;
        static final int NOT_LAST = 0;

        private double weightx;

        Row(double weightx) {
            super();
            this.setBackground(Color.WHITE);
            this.setForeground(Color.BLACK);
            this.weightx = weightx;
            this.setLayout(new GridLayout(ROWS_COUNT + 1, 1));
        }

        GridBagConstraints getConstraints(int rowPosition) {
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = weightx;
            c.weighty = 1.0;
            c.fill = GridBagConstraints.BOTH;

            if (rowPosition == LAST) c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridheight = GridBagConstraints.REMAINDER;
            return c;
        }
    }

    private Row[] rows = new Row[5];
    private double[] weights = {1.0, 3.0, 5.0, 3.0, 5.0};

    MainTable() {
        super();
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        for (int i = 0; i < rows.length; i++) {
            rows[i] = new Row(weights[i]);
            int position = (i == rows.length - 1) ? Row.LAST : Row.NOT_LAST;
            this.add(rows[i], rows[i].getConstraints(position));
        }

        rows[0].add(new Cell("#"));
        rows[1].add(new Cell("Имя"));
        rows[2].add(new Cell("Дата рождения"));
        rows[3].add(new Cell("Гендер"));
        rows[4].add(new Cell("Локация и координаты"));
    }
}
