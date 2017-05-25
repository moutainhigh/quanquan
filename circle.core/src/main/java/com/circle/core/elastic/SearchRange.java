package com.circle.core.elastic;

import java.io.Serializable;

/**
 * @author Created by cxx on 15-7-30.
 */
@SuppressWarnings("unused")
public class SearchRange implements Serializable {
    private String field;
    private Object from;
    private Object to;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getFrom() {
        return from;
    }

    public void setFrom(Object from) {
        this.from = from;
    }

    public Object getTo() {
        return to;
    }

    public void setTo(Object to) {
        this.to = to;
    }
}
