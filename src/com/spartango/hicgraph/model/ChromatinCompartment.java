package com.spartango.hicgraph.model;

public class ChromatinCompartment {
    public static final int OPEN   = 1;
    public static final int CLOSED = -1;

    private long            start;
    private long            end;

    private int             state;

    public ChromatinCompartment(long start, long end, int state) {
        this.start = start;
        this.end = end;
        this.state = state;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public int getState() {
        return state;
    }

    @Override
    public boolean equals(Object arg0) {
        return this == arg0
               || (arg0 instanceof ChromatinCompartment
                   && ((ChromatinCompartment) arg0).state == state
                   && ((ChromatinCompartment) arg0).start == start && ((ChromatinCompartment) arg0).end == end);

    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + state;
        hash = hash * 31 + (int) (start ^ (start >>> 32));
        hash = hash * 31 + (int) (end ^ (end >>> 32));
        return hash;
    }
}
