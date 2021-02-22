package com.bobisonfire;

public class ClosedIntegerHashSet {
    private static final int DEFAULT_CAPACITY = 16;
    private static final int MIN_CAPACITY = DEFAULT_CAPACITY;
    private static final double MAX_FILL_RATE = 0.5;
    private static final double MIN_FILL_RATE = 0.125;

    private int[] hashArray;
    private boolean[] deletedCells;
    private boolean[] busyCells;

    private int capacity;
    private int size;

    public ClosedIntegerHashSet() {
        this(DEFAULT_CAPACITY);
    }

    public ClosedIntegerHashSet(int capacity) {
        this.hashArray = new int[capacity];
        this.deletedCells = new boolean[capacity];
        this.busyCells = new boolean[capacity];

        this.capacity = capacity;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(int num) {
        return indexOf(num) > 0;
    }

    public boolean add(int num) {
        int i = num % capacity;
        while (busyCells[i]) {
            if (hashArray[i] == num) return false;
            i = (i + 1) % capacity;
        }

        hashArray[i] = num;
        size++;

        busyCells[i] = true;
        deletedCells[i] = false;

        stabilizeCapacity();
        return true;
    }

    public boolean remove(int num) {
        int i = indexOf(num);
        if (i == -1) return false;

        size--;

        busyCells[i] = false;
        deletedCells[i] = true;

        stabilizeCapacity();
        return true;
    }

    public void clear() {
        this.hashArray = new int[capacity];
        this.deletedCells = new boolean[capacity];
        this.busyCells = new boolean[capacity];

        this.size = 0;
    }

    private void stabilizeCapacity() {
        double fillRate = size * 1.0 / capacity;
        int oldCapacity = capacity;
        if (fillRate > MAX_FILL_RATE) capacity *= 2;
        if (capacity > MIN_CAPACITY && fillRate < MIN_FILL_RATE) capacity /= 2;

        if (oldCapacity == capacity) return;

        int[] oldHashArray = hashArray;
        boolean[] oldBusyCells = busyCells;

        clear();
        for (int i = 0; i < oldHashArray.length; i++) {
            if (oldBusyCells[i]) add(oldHashArray[i]);
        }
    }

    private int indexOf(int num) {
        int start = num % capacity;
        int i = start;
        while (deletedCells[i] || busyCells[i] && hashArray[i] != num) {
            i = (i + 1) % capacity;
            if (i == start) break;
        }

        if (busyCells[i] && hashArray[i] == num) return i;
        return -1;
    }
}
