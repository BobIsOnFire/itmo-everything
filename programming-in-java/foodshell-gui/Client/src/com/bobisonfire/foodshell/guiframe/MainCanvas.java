package com.bobisonfire.foodshell.guiframe;

import java.awt.*;

class MainCanvas extends Canvas {
    MainCanvas() {
        super();
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
    }

    @Override
    public void paint(Graphics g) {
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        drawMan(g, 40, 30, 100, Color.PINK);
        drawMan(g, 100, 10, 50, Color.GREEN);
    }

    private void drawMan(Graphics g, int x, int y, int size, Color color) {
        Color previous = g.getColor();
        int diameter = size / 4;

        Point head = new Point(x + size / 8, y);
        Point neck = new Point(x + size / 4, y + size / 4);

        Point leftHand = new Point(x, y + size * 5 / 8);
        Point rightHand = new Point(x + size / 2, y + size * 5 / 8);

        Point torso = new Point(x + size / 4, y + size * 5 / 8);
        Point leftFoot = new Point(x, y + size);
        Point rightFoot = new Point(x + size / 2, y + size);

        g.setColor(color);
        g.drawLine(neck.x, neck.y, leftHand.x, leftHand.y); // left hand
        g.drawLine(neck.x, neck.y, rightHand.x, rightHand.y); // right hand
        g.drawLine(neck.x, neck.y, torso.x, torso.y); // torso
        g.drawLine(torso.x, torso.y, leftFoot.x, leftFoot.y); // left foot
        g.drawLine(torso.x, torso.y, rightFoot.x, rightFoot.y); // right foot
        g.fillOval(head.x, head.y, diameter, diameter); // head

        g.setColor(previous);
    }
}
