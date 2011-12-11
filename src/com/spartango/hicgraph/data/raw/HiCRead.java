package com.spartango.hicgraph.data.raw;

import java.io.Serializable;

import com.spartango.ensembl.model.Gene;

public class HiCRead implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5479832539919693966L;

    private String            name;

    private final int         firstChromosome;
    private final long        firstPosition;
    private final int         firstStrand;
    private final Gene        firstGene;

    private final int         secondChromosome;
    private final long        secondPosition;
    private final int         secondStrand;
    private final Gene        secondGene;

    public HiCRead(String name,
                   int firstChromosome,
                   long firstPosition,
                   int firstStrand,
                   int secondChromosome,
                   long secondPosition,
                   int secondStrand) {
        
        this(name, firstChromosome, firstPosition, firstStrand, null,
             secondChromosome, secondPosition, secondStrand, null);
    }

    public HiCRead(String name,
                   int firstChromosome,
                   long firstPosition,
                   int firstStrand,

                   Gene firstGene,
                   int secondChromosome,
                   long secondPosition,
                   int secondStrand,

                   Gene secondGene) {

        this.name = name;
        this.firstChromosome = firstChromosome;
        this.firstPosition = firstPosition;
        this.firstStrand = firstStrand;
        this.firstGene = firstGene;
        this.secondChromosome = secondChromosome;
        this.secondPosition = secondPosition;
        this.secondStrand = secondStrand;
        this.secondGene = secondGene;
    }

    public String getName() {
        return name;
    }

    public int getFirstChromosome() {
        return firstChromosome;
    }

    public long getFirstPosition() {
        return firstPosition;
    }

    public int getFirstStrand() {
        return firstStrand;
    }

    public Gene getFirstGene() {
        return firstGene;
    }

    public int getSecondChromosome() {
        return secondChromosome;
    }

    public int getSecondStrand() {
        return secondStrand;
    }

    public long getSecondPosition() {
        return secondPosition;
    }

    public Gene getSecondGene() {
        return secondGene;
    }
}
