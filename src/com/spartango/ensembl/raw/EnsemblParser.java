package com.spartango.ensembl.raw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.spartango.ensembl.model.Gene;
import com.spartango.ensembl.model.Genome;
import com.spartango.hicgraph.model.ChromatinLocation;

public class EnsemblParser {

    public static Genome buildGenome(String gtffilename,
                                     String compartmentPrefix, int resolution) {

        Genome genome = new Genome(ChromatinLocation.NUM_CHROMOSOMES,
                                   compartmentPrefix, resolution);

        System.out.println("Building genome from GTF");

        // Open file for read
        int lines = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(gtffilename));

            HashMap<String, Gene> genes = new HashMap<String, Gene>();

            // For each line
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Parse the GTF line
                String[] parts = line.split("\t");
                if (parts.length == 9) {

                    // Get gene name
                    String geneName = getGeneNameFromAnnotation(parts[8]);
                    long start = Long.parseLong(parts[3]);
                    long end = Long.parseLong(parts[4]);

                    if (geneName != null) {

                        // Check if we've seen this gene before
                        Gene newGene = genes.get(geneName);
                        if (newGene == null) {
                            // Create a new gene
                            int chromosome = parseChromosome(parts[0]);
                            String classification = parts[1];

                            if (chromosome != 0) {
                                newGene = new Gene(chromosome, geneName,
                                                   classification);
                                // Set start and end
                                newGene.setStart(start);
                                newGene.setEnd(end);

                                genes.put(geneName, newGene);
                                genome.getChromosome(chromosome)
                                      .addGene(start, newGene);
                            }
                        } else {
                            if (newGene.getStart() > start) {
                                newGene.setStart(start);
                            }
                            if (newGene.getEnd() < end) {
                                newGene.setEnd(end);
                            }
                        }
                    }

                } else {
                    System.err.println("Error reading GTF line " + lines
                                       + ": not enough fields");
                }
                lines++;
            }
            System.out.println("Completed Genome parsing: " + lines + " -> "
                               + genes.size());
            // Close the file
            reader.close();
        } catch (FileNotFoundException e1) {
            System.err.println("Failed to find GTF file: " + gtffilename);
        } catch (IOException e) {
            System.err.println("Got stuck while trying to read GTF file @line "
                               + lines);
        }

        return genome;
    }

    private static String getGeneNameFromAnnotation(String annotations) {
        String[] parts = annotations.split(" ");
        if (parts.length > 8)
            return parts[8].substring(0, parts[8].length() - 1);
        else {
            System.err.println("No gene name in annotations");
            return null;
        }
    }

    private static int parseChromosome(String target) {
        int chromosome = 0;
        try {
            // Get chromosome
            chromosome = Integer.parseInt(target);
        } catch (NumberFormatException e) {
            if (target.equals("X")) {
                // X Chromosome
                chromosome = 23;
            } else if (target.equals("Y")) {
                // Y Chromosome
                chromosome = 24;
            }
        }
        return chromosome;
    }
}
