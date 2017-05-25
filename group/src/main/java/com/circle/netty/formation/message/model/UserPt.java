package com.circle.netty.formation.message.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Created by cxx on 15-10-24.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserPt {
    public static final String table_el = "user_pt";
    public static final int type_time = 2;
    public static final int type_price = 1;
    private int type;
    private long start;
    private long end;

    public UserPt() {
    }

    public UserPt(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
