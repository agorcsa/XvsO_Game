package com.example.xvso.Objects;

public class Cell {

    // cell state:
    // 0 = empty
    // 1 = X
    // 2 = O
    public static final int cellIsEmpty = 0;
    public static final int cellIsX = 1;
    public static final int cellIsO = 2;
    // represents the tag of each cell of the grid:
    // (0, 1, 2)
    // (3, 4, 5)
    // (6, 7, 8)
    private int tag;
    // can have 2 values:
    // true
    // false
    private boolean isClickable;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }
}
