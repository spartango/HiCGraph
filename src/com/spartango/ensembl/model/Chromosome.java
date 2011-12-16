package com.spartango.ensembl.model;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.spartango.hicgraph.data.raw.HiCCompartmentMap;
import com.spartango.hicgraph.model.ChromatinCompartment;

public class Chromosome {

    private TreeMap<Long, Gene>   genes;
    private HashMap<String, Gene> geneNames;

    private HiCCompartmentMap     compartments;
    private int                   number;

    public Chromosome(int i) {
        this(i, new HiCCompartmentMap());
    }

    public Chromosome(int i, HiCCompartmentMap compartments) {
        number = i;
        genes = new TreeMap<Long, Gene>();
        geneNames = new HashMap<String, Gene>();
        this.compartments = compartments;
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

    public ChromatinCompartment getComparment(long position) {
        return compartments.getComparment(position);
    }
    
    
}
