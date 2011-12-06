package com.spartango.hicgraph.data;

import java.io.Serializable;

public class HiCRead implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5479832539919693966L;

    private String     name;

    private final int  firstChromosome;
    private final long firstPosition;
    private final int  firstStrand;
    private final int  firstRestrictionFragment;

    private final int  secondChromosome;
    private final long secondPosition;
    
    private final int secondStrand;              
    private final int secondRestrictionFragment; 
    
    public HiCRead(String name,
                   int firstChromosome,
                   long firstPosition,
                   int firstStrand,
                   int firstRestrictionFragment,
                   int secondChromosome,
                   long secondPosition,
                   int secondStrand,
                   int secondRestrictionFragment) {

        this.name                      = name;
        this.firstChromosome           = firstChromosome;
        this.firstPosition             = firstPosition;
        this.firstStrand               = firstStrand;
        this.firstRestrictionFragment  = firstRestrictionFragment;
        this.secondChromosome          = secondChromosome;
        this.secondPosition            = secondPosition;
        this.secondStrand              = secondStrand;
        this.secondRestrictionFragment = secondRestrictionFragment;
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

    public int getFirstRestrictionFragment() {
        return firstRestrictionFragment;
    }

    public int getSecondChromosome() {
        return secondChromosome;
    }

    public int getSecondStrand() {
        return secondStrand;
    }

    public int getSecondRestrictionFragment() {
        return secondRestrictionFragment;
    }

    public long getSecondPosition() {
        return secondPosition;
    }
    
    

}
