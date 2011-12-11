package com.spartango.ensembl.model;

import java.util.Map.Entry;
import java.util.TreeMap;

public class Chromosome {

    private TreeMap<Long, Gene> genes;
    private int                 number;

    public Chromosome(int i) {
        number = i;
        genes = new TreeMap<Long, Gene>();
    }

    public Gene getGeneForPosition(long firstPosition) {
        Entry<Long, Gene> candidate = genes.floorEntry(firstPosition);
        if (candidate != null && candidate.getValue().getEnd() >= firstPosition) {
            return candidate.getValue();
        } else
            return null;
    }

    public void addGene(long start, Gene newGene) {
        genes.put(start, newGene);

    }
}
