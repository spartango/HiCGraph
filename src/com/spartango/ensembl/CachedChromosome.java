package com.spartango.ensembl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import uk.ac.roslin.ensembl.exception.DAOException;
import uk.ac.roslin.ensembl.model.core.Chromosome;
import uk.ac.roslin.ensembl.model.core.Gene;

public class CachedChromosome {

    private Chromosome rawChromosome;

    private Lock       chromosomeLock;

    public CachedChromosome(Chromosome c) {
        rawChromosome = c;
        chromosomeLock = new ReentrantLock();
    }

    public Gene getGeneForPosition(int position) {

        // If its not there, hit the db
        List<? extends Gene> candidates = getCandidates(position);
        if (!candidates.isEmpty()) {
            // Cache the candidate
            Gene first = candidates.get(0);
            // cacheGene(first);
            System.out.println(position + " => " + first.getDescription());

            return first;
        } else {
            return null;
        }
    }

    private List<? extends Gene> getCandidates(int position) {
        List<? extends Gene> candidates = null;
        chromosomeLock.lock();
        try {
            candidates = rawChromosome.getGenesOnRegion(position, position);
        } catch (DAOException e) {
            System.err.println("Chromosome cache couldn't search Ensembl: DAO issue");
        }
        chromosomeLock.unlock();
        return candidates;
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
