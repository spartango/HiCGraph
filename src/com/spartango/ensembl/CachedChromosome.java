package com.spartango.ensembl;

import java.util.TreeMap;

import uk.ac.roslin.ensembl.model.core.Chromosome;
import uk.ac.roslin.ensembl.model.core.Gene;

public class CachedChromosome {

    private Chromosome             rawChromosome;
    // Key is start site
    private TreeMap<Integer, Gene> geneCache;

    public CachedChromosome(Chromosome c) {
        rawChromosome = c;
        geneCache = new TreeMap<Integer, Gene>();
    }

    public Gene getGeneForPosition(int position) {
        // First try to find the gene in the cache
        // TODO 
        // If its not there, hit the db
        return null;
    }

    public static int getGeneStart(Gene target) {
        try {
            return target.getMappings().first().getTargetCoordinates().getStart();
        } catch (NullPointerException e) {
            return -1;
        }
    }

}
