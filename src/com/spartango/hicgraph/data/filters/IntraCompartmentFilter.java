package com.spartango.hicgraph.data.filters;

import com.spartango.ensembl.model.Genome;
import com.spartango.hicgraph.data.raw.HiCRead;
import com.spartango.hicgraph.model.ChromatinCompartment;

public class IntraCompartmentFilter implements ReadFilter {

    private Genome genome;

    public IntraCompartmentFilter(Genome genome) {
        this.genome = genome;
    }

    @Override
    public boolean check(HiCRead r) {
        // Look up the compartments for each read
        ChromatinCompartment firstCompartment = genome.getChromosome(r.getFirstChromosome())
                                                      .getComparment(r.getFirstPosition());
        ChromatinCompartment secondCompartment = genome.getChromosome(r.getSecondChromosome())
                                                       .getComparment(r.getSecondPosition());
        // if (firstCompartment == null || secondCompartment == null) {
        // System.err.println("No compartment for " + r.getFirstChromosome()
        // + ":" + r.getFirstPosition() + " -> "
        // + firstCompartment + " & "
        // + r.getSecondChromosome() + ":"
        // + r.getSecondPosition() + " -> "
        // + secondCompartment);
        // }
        return (firstCompartment != null && secondCompartment != null && firstCompartment == secondCompartment );
    }
}
