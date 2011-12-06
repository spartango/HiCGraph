package com.spartango.hicgraph.model;

public class ChromatinLocation {
    // Strand types
    public static final int WATSON = 0;
    public static final int CRICK  = 1;

    private int             chromosome;
    private long            position;
    private int             strand;

    public ChromatinLocation(int chromosome, long position, int strand) {
        this.chromosome = chromosome;
        this.position = position;
        this.strand = strand;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        if (obj instanceof ChromatinLocation) {
            ChromatinLocation target = (ChromatinLocation) obj;
            if (target.strand == this.strand
                && target.chromosome == this.chromosome
                && target.position == this.position)
                return true;
            else
                return false;
        } else
            return false;
    }
}
