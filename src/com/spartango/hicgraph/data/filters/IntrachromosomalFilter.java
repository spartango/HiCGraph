package com.spartango.hicgraph.data.filters;

import com.spartango.hicgraph.data.raw.HiCRead;

public class IntrachromosomalFilter implements ReadFilter {

    private int chromosome;
    
    
    public IntrachromosomalFilter() {
        this(-1);
    }
    
    public IntrachromosomalFilter(int chromosome) {
        this.chromosome = chromosome;
    }


    @Override
    public boolean check(HiCRead r) {
        return ((r.getFirstChromosome() == r.getSecondChromosome()) 
                && (chromosome != -1 ?  
                                       (r.getFirstChromosome() == chromosome)  
                                       : true));
    }

}
