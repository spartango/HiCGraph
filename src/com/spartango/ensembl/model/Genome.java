package com.spartango.ensembl.model;

public class Genome {

    private Chromosome[] chromosomes;

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
