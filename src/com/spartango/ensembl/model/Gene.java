package com.spartango.ensembl.model;

public class Gene {

    // Types
    public static final int CDS         = 0;
    public static final int START_CODON = 1;
    public static final int STOP_CODON  = 2;
    public static final int THREE_UTR   = 3;
    public static final int FIVE_UTR    = 4;
    public static final int INTER       = 5;
    public static final int INTER_CNS   = 6;
    public static final int INTRON_CNS  = 7;
    public static final int EXON        = 8;

    private int             chromosome;
    private long            start;
    private long            end;
    private String          name;
    private String          classification;

    // TODO elements

    public Gene(int chromsome, String name, String classification) {
        this.chromosome = chromsome;
        this.name = name;
        this.classification = classification;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public String getClassification() {
        return classification;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getDescription() {
        return name;
    }

    public int getChromosome() {
        return chromosome;
    }

}
