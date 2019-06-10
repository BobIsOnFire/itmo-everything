package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.entities.Coordinate;
import com.bobisonfire.foodshell.client.entities.Location;
import com.bobisonfire.foodshell.client.entities.User;
import com.bobisonfire.foodshell.client.exchange.Request;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class MainCanvas extends Canvas {

    MainCanvas() {
        super();
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        this.setBounds(660, 400, 600, 300);
    }

    @Override
    public void paint(Graphics g) {
        Object[] list = Request.execute(Request.SORT, User.class, "id", "ASC");
        List<User> userList = Arrays.stream(list).map(elem -> (User) elem ).collect(Collectors.toList());

        while (MainFrame.locationList.size() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

        Location location = (Location) MainFrame.locationBox.getSelectedItem();
        if (location == null) return;

        MainFrame.humanList.stream() // todo can I put a human in this location in these coordinates?
                .filter(elem -> elem.getLocationID() == location.getId())
                .forEach(elem -> {
                    User user = userList.stream()
                            .filter(e -> e.getId() == elem.getCreatorID())
                            .findFirst().orElse(new User());

                    Coordinate locCrd = location.getCoordinate();
                    Coordinate elemCrd = elem.getCoordinate();
                    Coordinate relativeCrd = new Coordinate(
                            (elemCrd.getX() - locCrd.getX()) / location.getSize(),
                            (elemCrd.getY() - locCrd.getY()) / location.getSize(),
                            (elemCrd.getZ() - locCrd.getZ()) / location.getSize()
                            );
                    int x = 20 + (int) ( ( getWidth() - 40 ) * ( 0.5 + 1.0/3 * relativeCrd.getX() - 1.0/6 * relativeCrd.getY() ) );
                    int y = 20 + (int) ( ( getHeight() - 40 ) * ( 0.5 - 1.0/3 * relativeCrd.getZ() + 1.0/6 * relativeCrd.getY() ) );
                    int size = (int) ( 40 * Math.pow(2, relativeCrd.getY()) );
                    System.out.printf("%s %s %d %d %d\n", elemCrd, relativeCrd, x, y, size);

                    drawMan( g, x, y, size, new Color(user.getColor()) );
                });
    }

    private void drawMan(Graphics g, int x, int y, int size, Color color) {
        Color previous = g.getColor();
        int diameter = size / 4;

        Point head = new Point(x + size / 8, y - size);
        Point neck = new Point(x + size / 4, y - size * 3 / 4);

        Point leftHand = new Point(x, y - size * 3 / 8);
        Point rightHand = new Point(x + size / 2, y - size * 3 / 8);

        Point torso = new Point(x + size / 4, y - size * 3 / 8);
        Point leftFoot = new Point(x, y);
        Point rightFoot = new Point(x + size / 2, y);

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
