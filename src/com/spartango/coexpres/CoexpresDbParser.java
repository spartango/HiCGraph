package com.spartango.coexpres;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.spartango.ensembl.raw.EntrezMapParser;

import edu.uci.ics.jung.graph.util.Pair;

public class CoexpresDbParser {

    public static HashMap<Pair<String>, Double> parseDb(String dbFilename,
                                                        String entrezFilename) {
        HashMap<Integer, String> entrezMap = EntrezMapParser.parseMap(entrezFilename);

        HashMap<Pair<String>, Double> map = new HashMap<Pair<String>, Double>();
        System.out.println("Building Coexpres Db");

        int lines = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(dbFilename));

            String line = null;
            while ((line = reader.readLine()) != null) {
                // Parse the GTF line
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    try {
                        Integer firstGeneId = Integer.parseInt(parts[0]);
                        Integer secondGeneId = Integer.parseInt(parts[1]);

                        double pearsonVal = Double.parseDouble(parts[3]);

                        String firstGeneName = entrezMap.get(firstGeneId);
                        String secondGeneName = entrezMap.get(secondGeneId);

                        if (firstGeneName != null && secondGeneName != null) {
                            map.put(new Pair<String>(firstGeneName,
                                                     secondGeneName),
                                    pearsonVal);
                        }

                    } catch (NumberFormatException e) {
                        System.err.println("Formatting issue on line " + lines
                                           + " of CoexpresDb");
                    }
                } else {
                    System.err.println("Not enough fields on line " + lines
                                       + " of CoexpresDb");
                }
                lines++;
            }

            // Close the file
            reader.close();
        } catch (FileNotFoundException e1) {
            System.err.println("Failed to find coexpresdb file: " + dbFilename);
        } catch (IOException e) {
            System.err.println("Got stuck while trying to read coexpres file @line "
                               + lines);
        }

        System.out.println("Built Coexpres Db " + lines + " -> " + map.size());
        return map;
    }
}
