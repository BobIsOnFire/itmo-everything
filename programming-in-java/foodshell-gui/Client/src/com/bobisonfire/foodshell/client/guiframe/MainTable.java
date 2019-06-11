package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.entities.Coordinate;
import com.bobisonfire.foodshell.client.entities.Gender;
import com.bobisonfire.foodshell.client.entities.Human;
import com.bobisonfire.foodshell.client.entities.Location;
import com.bobisonfire.foodshell.client.exchange.Request;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class MainTable extends JPanel {
    private static final Object[] columnNames = {"#", "Имя", "Дата рождения", "Гендер", "Локация и координаты"};
    private static final double[] percentage = {0.04, 0.12, 0.24, 0.24, 0.36};
    private static String[] newHuman = {"<>", "<Создать>", "<Создать>", "<Создать>", "<Создать>"};
    private static boolean[] newHumanCreated = {false, false, false, false};

    private int position;
    private int createIndex;
    private JTable table;

    private int currentSorterColumn = 0;
    private boolean ascending = true;

    MainTable() {
        super();
        this.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        position = 0;
        table = new JTable();
        fillData();
        table.addMouseWheelListener(new TableScrollListener());
        table.addMouseListener(new CellPopupListener());

        table.getTableHeader().addMouseListener(new HeaderClickListener());
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        this.add(table.getTableHeader());
        this.add(table);
    }

    private void fillData() {
        while (MainFrame.locationList.size() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<Location> sortedList = MainFrame.locationList.stream()
                .sorted( Comparator.comparingInt(Location::getId) )
                .collect(Collectors.toList());

        List<Location> locations = MainFrame.humanList.stream()
                .map(elem -> sortedList.get(elem.getLocationID() - 1))
                .collect(Collectors.toList());

        Iterator<Human> iter = MainFrame.humanList.iterator();

        Object[][] rowData = new Object[15][5];
        int i = 0;
        while (iter.hasNext() && i < position) {
            iter.next();
            i++;
        }

        i = 0;
        while (iter.hasNext() && i < 15) {
            Human human = iter.next();
            Coordinate crd = human.getCoordinate();
            String location = locations.get(i).getName() + " " + crd;
            rowData[i] = new Object[]
                    {String.valueOf( human.getId() ), human.getName(), human.getBirthday(), human.getGender().getName(), location };
            i++;
        }

        if (i < 15)
            rowData[i] = newHuman;
        createIndex = i;

        table.setModel(new CustomTableModel(rowData, columnNames));
        setTableSize();
    }

    private void setTableSize() {
        int width = 600;
        TableColumnModel model = table.getColumnModel();
        for (int i = 0; i < 5; i++) {
            model.getColumn(i).setPreferredWidth((int) (width * percentage[i]));
        }
        table.setRowHeight(36);
    }

    private class TableScrollListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int notches = e.getWheelRotation();
            if (notches < 0)
                position -= (position == 0) ? 0 : 1;
            else
                position += (position == MainFrame.humanList.size() - 1) ? 0 : 1;
            fillData();
        }
    }

    private class CellChangeListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (row == createIndex && e.getType() == TableModelEvent.UPDATE) {
                String cellValue = (String) table.getModel().getValueAt(row, column);
                if (!cellValue.isEmpty()) {
                    newHuman[column] = cellValue;
                    newHumanCreated[column - 1] = true;
                }
                else {
                    newHuman[column] = "<Создать>";
                    table.getModel().setValueAt("<Создать>", row, column);
                    newHumanCreated[column - 1] = false;
                }

                int i = 0;
                while (i < 4 && newHumanCreated[i]) i++;
                if (i < 4) return;

                Human human = createHuman(newHuman);
                Request.execute(Request.SET, Human.class, human);
                new Thread(new TableSortUpdater("id", "ASC")).start();
                return;
            }

            int id = Integer.parseInt( (String) table.getModel().getValueAt(row, 0) );
            // todo id is constantly going up!!!
            if (e.getType() == TableModelEvent.UPDATE) {
                String[] data = new String[5];
                for (int i = 0; i < 5; i++) {
                    data[i] = (String) table.getModel().getValueAt(row, i);
                }
                Human newHuman = createHuman(data);
                Request.execute(Request.REMOVE, Human.class, id);
                Request.execute(Request.SET, Human.class, newHuman);
                new Thread(new TableSortUpdater("id", "ASC")).start();
            }
        }

        private Human createHuman(String[] data) {
            Human human = new Human(); // todo check if human exists
            String name = data[1].trim();
            String birthday = data[2].trim();
            Gender gender = Gender.getGenderByName( data[3].trim() ); // todo add list of genders
            String[] tokens = data[4].split("\\s+", 2);

            Object[] list = Request.execute(Request.GET, Location.class, "name", tokens[0]);
            Location location = (Location) list[0];
            Coordinate coordinate = Coordinate.from(tokens[1]); // todo check if data is valid

            human.setName(name);
            human.setCreatorID(MainFrame.user.getId());
            human.setCreationDate( ZonedDateTime.now().toString() );
            human.setGender(gender);
            human.setLocationID(location.getId());
            human.setBirthday( birthday );
            human.setCoordinate(coordinate);

            return human;
        }
    }

    private class CustomTableModel extends DefaultTableModel {
        CustomTableModel(Object[][] rowData, Object[] columnNames) {
            super(rowData, columnNames);
            this.addTableModelListener(new CellChangeListener());
        }
        @Override
        public boolean isCellEditable(int row, int column) {
            if (row > createIndex) return false;
            if (column == 0) return false;
            if (row == createIndex) return true;
            return MainFrame.humanList.get(row + position).getCreatorID() == MainFrame.user.getId();
        }
    }

    class TableSortUpdater implements Runnable {
        private String field;
        private String order;

        TableSortUpdater(String field, String order) {
            this.field = field;
            this.order = order;
        }

        @Override
        public void run() {
            Object[] list = Request.execute(Request.SORT, Human.class, field, order);
            MainFrame.humanList = Arrays.stream(list).map( elem -> (Human) elem).collect(Collectors.toList());
            list = Request.execute(Request.SORT, Location.class, "id", "ASC");
            MainFrame.locationList = Arrays.stream(list).map( elem -> (Location) elem).collect(Collectors.toList());

            position = 0;
            newHuman = new String[] {"<>", "<Создать>", "<Создать>", "<Создать>", "<Создать>"};
            newHumanCreated = new boolean[] {false, false, false, false};
            fillData();
            MainFrame.canvas.repaint();
        }
    }

    class TableFilterUpdater implements Runnable {
        private String field;
        private int value;

        TableFilterUpdater(String field, int value) {
            this.field = field;
            this.value = value;
        }

        @Override
        public void run() {
            Object[] list = Request.execute(Request.FILTER, Human.class, field, value);
            MainFrame.humanList = Arrays.stream(list).map( elem -> (Human) elem).collect(Collectors.toList());
            list = Request.execute(Request.SORT, Location.class, "id", "ASC");
            MainFrame.locationList = Arrays.stream(list).map( elem -> (Location) elem).collect(Collectors.toList());

            currentSorterColumn = -1;
            position = 0;
            newHuman = new String[] {"<>", "<Создать>", "<Создать>", "<Создать>", "<Создать>"};
            newHumanCreated = new boolean[] {false, false, false, false};
            fillData();
            MainFrame.canvas.repaint();
        }
    }

    private class HeaderClickListener extends MouseAdapter {
        private final String[] sorterFields = {"id", "name", "birthday", "gender", "y"};
        @Override
        public void mouseClicked(MouseEvent e) { // todo add filter there (smh)
            int column = table.columnAtPoint(e.getPoint());
            if (SwingUtilities.isLeftMouseButton(e)) {

                if (column == currentSorterColumn) ascending = !ascending;
                else ascending = true;
                currentSorterColumn = column;

                new Thread(new TableSortUpdater(sorterFields[column], ascending ? "ASC" : "DESC")).start();
            }
            else if (SwingUtilities.isRightMouseButton(e) && (column == 3 || column == 4)) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem item = new JMenuItem("Фильтр...");

                item.addActionListener(new FilterListener(column));
                menu.add(item);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private class FilterListener implements ActionListener {
        private int column;
        FilterListener(int column) {
            this.column = column;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame selectFrame = new JFrame("Фильтр");
            selectFrame.setBounds(400, 400, 300, 200);
            Container container = selectFrame.getContentPane();
            container.setLayout(new GridBagLayout());

            Set<Object> set = new HashSet<>();
            if (column == 3)
                set = MainFrame.humanList.stream()
                        .map(Human::getGender)
                        .collect(Collectors.toSet());
            if (column == 4)
                set = MainFrame.humanList.stream()
                        .map(elem -> {
                            int location_id = elem.getLocationID();
                            return MainFrame.locationList.stream()
                                    .filter(loc -> loc.getId() == location_id)
                                    .findFirst().orElse(new Location());
                        })
                        .collect(Collectors.toSet());

            JLabel infoLabel = CustomComponentFactory.getLabel("", SwingConstants.CENTER, 16.0f, false);
            infoLabel.setText("Выберите фильтр из списка:");
            JComboBox selectBox = CustomComponentFactory.getComboBox( set.toArray() );
            JButton cancelButton = new JButton();
            JButton okButton = new JButton();

            cancelButton.setAction(new AbstractAction("Отмена") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectFrame.setVisible(false);
                    selectFrame.dispose();
                }
            });

            okButton.setAction(new AbstractAction("ОК") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object result = selectBox.getSelectedItem();
                    if (result == null) {
                        infoLabel.setText("Ничего не выбрано.");
                        return;
                    }

                    String field;
                    int value;
                    if (column == 3) {
                        field = "gender";
                        value = ( (Gender) result ).ordinal();
                    } else {
                        field = "location_id";
                        value = ( (Location) result ).getId();
                    }

                    selectFrame.setVisible(false);
                    selectFrame.dispose();

                    new Thread(new TableFilterUpdater(field, value)).start();
                }
            });

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(10, 10, 10, 10);
            c.weightx = 2.0;
            c.weighty = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;

            container.add(infoLabel, c);
            container.add(selectBox, c);

            c.gridwidth = 1;
            c.weightx = 1.0;
            container.add(cancelButton, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            container.add(okButton, c);

            selectFrame.setVisible(true);

        }
    } // todo customize buttons

    private class CellPopupListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int column = table.columnAtPoint(e.getPoint());
            if (row < 0 || column < 0)
                return;

            if (SwingUtilities.isRightMouseButton(e) && table.getModel().isCellEditable(row, 1)) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem removeItem = new JMenuItem("Удалить"); // todo add items to remove older/younger
                JMenuItem olderItem = new JMenuItem("Удалить старших");
                JMenuItem youngerItem = new JMenuItem("Удалить моложе");
                menu.add(removeItem);
                menu.add(olderItem);
                menu.add(youngerItem);

                removeItem.addActionListener( evt -> CustomComponentFactory.showChoice(
                        "Хотите удалить персонажа?",
                        new RemoveHuman(row)
                ));
                olderItem.addActionListener( evt -> CustomComponentFactory.showChoice(
                        "Хотите удалить персонажей, которые старше данного?",
                        new RemoveOlderHumans(row)
                ));
                youngerItem.addActionListener( evt -> CustomComponentFactory.showChoice(
                        "Хотите удалить персонажей, которые моложе данного?",
                        new RemoveYoungerHumans(row)
                ));
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private class RemoveHuman implements Supplier {
        int row;
        RemoveHuman(int row) {
            this.row = row;
        }

        @Override
        public Object get() {
            int id = Integer.parseInt( (String) table.getModel().getValueAt(row, 0) );
            Request.execute(Request.REMOVE, Human.class, id);
            new Thread(new TableSortUpdater("id", "ASC")).start();
            return null;
        }
    }

    private class RemoveOlderHumans implements Supplier {
        int row;
        RemoveOlderHumans(int row) {
            this.row = row;
        }
        @Override
        public Object get() {
            int id = Integer.parseInt( (String) table.getModel().getValueAt(row, 0) );
            Human human = (Human) Request.execute(Request.GET, Human.class, "id", String.valueOf(id))[0];
            String[] tokens = human.getBirthday().split("\\.");
            String date = tokens[2] + "-" + tokens[1] + "-" + tokens[0];
            Request.execute(Request.REMOVE_CONDITION, Human.class,
                    "birthday < '" + date + "' AND creator_id = " + human.getCreatorID());
            new Thread(new TableSortUpdater("id", "ASC")).start();
            return null;
        }
    }

    private class RemoveYoungerHumans implements Supplier {
        int row;
        RemoveYoungerHumans(int row) {
            this.row = row;
        }
        @Override
        public Object get() {
            int id = Integer.parseInt( (String) table.getModel().getValueAt(row, 0) );
            Human human = (Human) Request.execute(Request.GET, Human.class, "id", String.valueOf(id))[0];
            String[] tokens = human.getBirthday().split("\\.");
            String date = tokens[2] + "-" + tokens[1] + "-" + tokens[0];
            Request.execute(Request.REMOVE_CONDITION, Human.class,
                    "birthday > '" + date + "' AND creator_id = " + human.getCreatorID());
            new Thread(new TableSortUpdater("id", "ASC")).start();
            return null;
        }
    }}
