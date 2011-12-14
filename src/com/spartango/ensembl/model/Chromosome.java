package com.spartango.ensembl.model;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Chromosome {

    private TreeMap<Long, Gene>   genes;
    private HashMap<String, Gene> geneNames;
    private int                   number;

    public Chromosome(int i) {
        number = i;
        genes = new TreeMap<Long, Gene>();
        geneNames = new HashMap<String, Gene>();
    }

    public Gene getGeneForPosition(long firstPosition) {
        Entry<Long, Gene> candidate = genes.floorEntry(firstPosition);
        if (candidate != null && candidate.getValue().getEnd() >= firstPosition) {
            return candidate.getValue();
        } else
            return null;
    }

    public Gene getGeneForName(String name) {
        return geneNames.get(name);
    }

    public int getNumber() {
        return number;
    }

    public void addGene(long start, Gene newGene) {
        genes.put(start, newGene);
        geneNames.put(newGene.getName(), newGene);
    }
}
