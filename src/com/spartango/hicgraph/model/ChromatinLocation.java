package com.spartango.hicgraph.model;

public class ChromatinLocation {
    // Strand types
    public static final int WATSON = 0;
    public static final int CRICK  = 1;

    private int             chromosome;
    private int             position;
    private int             strand;

    public ChromatinLocation(int chromosome, int position, int strand) {
        this.chromosome = chromosome;
        this.position = position;
        this.strand = strand;
    }

}
