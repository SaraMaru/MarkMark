package com.example.zju.markmark;

/**
 * Created by 本 on 2017-11-27.
 */

public class MarkRelation {
    private String em1Text;
    private String em2Text;
    private String label;

    private int start1;
    private int end1;
    private int start2;
    private int end2;

    public MarkRelation(String em1Text, String label, int start1, int end1) {
        this.em1Text = em1Text;
        this.label = label;
        this.start1 = start1;
        this.end1 = end1;
    }

    public void setEm2Text(String em2Text, int start2, int end2) {
        this.em2Text = em2Text;
        this.start2 = start2;
        this.end2 = end2;
    }

    public int getStart1() {
        return start1;
    }

    public int getEnd1() {
        return end1;
    }

    public int getStart2() {
        return start2;
    }

    public int getEnd2() {
        return end2;
    }

    public String getLabel() {
        return label;
    }
}
