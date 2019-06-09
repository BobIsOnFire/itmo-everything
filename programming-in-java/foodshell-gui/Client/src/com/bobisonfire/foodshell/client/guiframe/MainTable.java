package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.entities.Coordinate;
import com.bobisonfire.foodshell.client.entities.Human;
import com.bobisonfire.foodshell.client.entities.Location;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

class MainTable extends JPanel {
    private static Object[] columnNames = {"#", "Имя", "Дата рождения", "Гендер", "Локация и координаты"};
    private static double[] percentage = {0.04, 0.12, 0.24, 0.24, 0.36};
    private Object[][] rowData;
    private int position;
    private JTable table;

    MainTable() {
        super();
        this.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        position = 0;
        rowData = new Object[15][5];
        fillData();
        table = new JTable(rowData, columnNames);
        this.add(table.getTableHeader());
        this.add(table);

    }

    private void fillData() {
        List<Location> sortedList = MainFrame.locationList.stream()
                .sorted( Comparator.comparingInt(Location::getId) )
                .collect(Collectors.toList());

        List<Location> locations = MainFrame.humanList.stream()
                .map(elem -> sortedList.get(elem.getLocationID()))
                .collect(Collectors.toList());

        Iterator<Human> iter = MainFrame.humanList.iterator();
        int i = 0;
        while (iter.hasNext() && i < 15) {
            Human human = iter.next();
            Coordinate crd = human.getCoordinate();
            String location = locations.get(i).getName() + " " + crd;
            rowData[i] = new Object[]
                    {human.getId(), human.getName(), human.getBirthday(), human.getGender(), location };
            i++;
        }
    }

    void setColumnWidth() {
        int width = 600;
        TableColumnModel model = table.getColumnModel();
        for (int i = 0; i < 5; i++) {
            model.getColumn(i).setPreferredWidth((int) (width * percentage[i]));
        }
        table.setRowHeight(36);
    }
}
