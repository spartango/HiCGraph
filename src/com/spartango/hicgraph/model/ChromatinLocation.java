package com.spartango.hicgraph.model;

import com.spartango.ensembl.model.Gene;

public class ChromatinLocation {
    // Strand types
    public static final int    WATSON             = 0;
    public static final int    CRICK              = 1;

    public static final int    NUM_CHROMOSOMES    = 24;

    public static final long[] CHROMOSOME_LENGTHS = {
            249250621,
            243199373,
            198022430,
            191154276,
            180915260,
            171115067,
            159138663,
            146364022,
            141213431,
            135534747,
            135006516,
            133851895,
            115169878,
            107349540,
            102531392,
            90354753,
            81195210,
            78077248,
            59128983,
            63025520,
            48129895,
            51304566,
            155270560,
            59373566                             };

    private final int          chromosome;
    private final long         position;
    private final int          strand;
    private final Gene         gene;

    public ChromatinLocation(int chromosome, long position, int strand) {
        this(chromosome, position, strand, null);
    }

    public ChromatinLocation(int chromosome,
                             long position,
                             int strand,
                             Gene gene) {
        this.chromosome = chromosome;
        this.position = position;
        this.strand = strand;
        this.gene = gene;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        if (obj instanceof ChromatinLocation) {
            ChromatinLocation target = (ChromatinLocation) obj;
            if (target.chromosome == this.chromosome
                && target.position == this.position)
                return true;
            else
                return false;
        } else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + chromosome;
        hash = hash * 31 + (int) (position ^ (position >>> 32));
        return hash;
    }

    public int getChromosome() {
        return chromosome;
    }

    public long getPosition() {
        return position;
    }

    public int getStrand() {
        return strand;
    }

    public Gene getGene() {
        return gene;
    }

    @Override
    public String toString() {
        return "[" + chromosome + ":" + position
               + (gene != null ? ": " + gene.getDescription() : "+") + "]";
    }
}
