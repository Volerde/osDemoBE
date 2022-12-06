package com.example.osdemo.pojo;

public class BDModel {
    private int id;
    private int start;
    private int end;
    private boolean status;

    public BDModel(int id, int start, int end,boolean status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isStatus() {
        return status;
    }
}
