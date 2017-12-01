package com.example.zju.markmark;

/**
 * Created by 本 on 2017-11-27.
 */

public class MarkEntity {
    private String label;
    private int start;
    private String text;

    private int end;

    public MarkEntity(String label, int start, int end, String text) {
        this.label = label;
        this.start = start;
        this.end = end;
        this.text = text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getLabel() {
        return label;
    }
}
