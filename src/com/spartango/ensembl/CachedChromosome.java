package com.spartango.ensembl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.roslin.ensembl.exception.DAOException;
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
        Map.Entry<Integer, Gene> candidate = geneCache.floorEntry(position);
        // Check that the position is within the gene length
        if (candidate != null && position < getGeneEnd(candidate.getValue())) {
            return candidate.getValue();

        } else {
            // If its not there, hit the db
            try {
                List<? extends Gene> candidates = rawChromosome.getGenesOnRegion(position,
                                                                                 position);
                if (!candidates.isEmpty()) {
                    // Cache the candidate
                    Gene first = candidates.get(0);
                    geneCache.put(getGeneStart(first), first);

                    // System.out.println("Cached gene: " +
                    // first.getDescription());

                    return first;
                }
            } catch (DAOException e) {
                System.err.println("Chromosome cache couldn't search Ensembl: DAO issue");
            }
            return null;
        }
    }

    public static int getGeneStart(Gene target) {
        try {
            return target.getMappings().first().getTargetCoordinates()
                         .getStart();
        } catch (NullPointerException e) {
            System.err.println("Failed to get gene start for "
                               + target.getDescription());
            return -1;
        }
    }

    public static int getGeneEnd(Gene target) {
        try {
            return target.getMappings().first().getTargetCoordinates().getEnd();
        } catch (NullPointerException e) {
            System.err.println("Failed to get gene start for "
                               + target.getDescription());
            return -1;
        }
    }
}
