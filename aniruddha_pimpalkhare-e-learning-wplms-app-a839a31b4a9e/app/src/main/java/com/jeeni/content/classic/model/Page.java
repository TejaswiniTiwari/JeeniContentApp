package com.jeeni.content.classic.model;


/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class Page {
    private static final String TAG =Page.class.getSimpleName();
    private int number;
    private boolean isSelected;

    /**
     * Initializes immutable unit object
     * @param number Title of unit
     * @return the id of a unit
     */
    public Page(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
