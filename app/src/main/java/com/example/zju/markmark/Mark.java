package com.example.zju.markmark;

import java.util.ArrayList;

/**
 * Created by 本 on 2017-11-27.
 */

public class Mark {
    private String articleID;
    private ArrayList<MarkEntity> entityMentions = new ArrayList<MarkEntity>();
    private ArrayList<MarkRelation> relationMentions = new ArrayList<MarkRelation>();
    private int sentID;
    private String sentText;

    public Mark(String articleID, int sentID, String sentText) {
        this.articleID = articleID;
        this.sentID = sentID;
        this.sentText = sentText;
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