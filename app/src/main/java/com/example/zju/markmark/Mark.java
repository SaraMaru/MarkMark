package com.example.zju.markmark;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 本 on 2017-11-27.
 */

public class Mark implements Serializable{
    private String articleID;
    private ArrayList<MarkEntity> entityMentions = new ArrayList<>();
    private ArrayList<MarkRelation> relationMentions = new ArrayList<>();
    private int sentID;
    private String sentText;

    public Mark(String articleID, int sentID, String sentText) {
        this.articleID = articleID;
        this.sentID = sentID;
        this.sentText = sentText;
    }

    public Mark() {
    }

    public int getSentID() {
        return sentID;
    }

    public void setEntityMentions(MarkEntity e) {
        this.entityMentions.add(e);
    }

    public void setRelationMentions(MarkRelation r) {
        this.relationMentions.add(r);
    }

    public ArrayList<MarkEntity> getEntityMentions() {
        return entityMentions;
    }

    public ArrayList<MarkRelation> getRelationMentions() {
        return relationMentions;
    }
} // take a sentence as a unit