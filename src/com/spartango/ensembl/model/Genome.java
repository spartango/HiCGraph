package com.spartango.ensembl.model;

import com.spartango.hicgraph.data.raw.HiCCompartmentMap;

public class Genome {

    private Chromosome[] chromosomes;

    public Genome(int chromosomeCount, String compartmentPrefix, int resolution) {
        chromosomes = new Chromosome[chromosomeCount];
        for (int i = 0; i < chromosomeCount; i++) {
            HiCCompartmentMap map = HiCCompartmentMap.parseFromFile((compartmentPrefix
                                                                     + "" + (i + 1)),
                                                                    resolution);
            chromosomes[i] = new Chromosome(i + 1, map);
        }
    }

    public Genome(int chromosomeCount) {
        chromosomes = new Chromosome[chromosomeCount];
        for (int i = 0; i < chromosomeCount; i++) {
            chromosomes[i] = new Chromosome(i + 1);
        }
    }

    public Chromosome getChromosome(int number) {
        return chromosomes[number - 1];
    }

    public int getChromosomeCount() {
        return chromosomes.length;
    }
}
