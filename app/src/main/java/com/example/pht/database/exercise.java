package com.example.pht.database;

public class exercise {

    private int id;
    private String name;
    private double met;

    public exercise(String name, Object object) {
        super();
        this.name = name;
        this.met = (Double) object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMet() {
        return met;
    }

    public void setMet(double met) {
        this.met = met;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}