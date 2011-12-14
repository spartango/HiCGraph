package com.spartango.ensembl.raw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class EntrezMapParser {

    public static HashMap<Integer, String> parseMap(String entrezFilename) {
        System.out.println("Building entrez map");
        HashMap<Integer, String> map = new HashMap<Integer, String>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(entrezFilename));

            // For each line
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    String name = parts[0];
                    try {
                        int id = Integer.parseInt(parts[1]);
                        map.put(id, name);
                    } catch (NumberFormatException e) {
                        System.err.println("No id for " + name);
                    }
                } else {
                    System.err.println("Not enough fields for mapping");
                }
            }
        } catch (FileNotFoundException e1) {
            System.err.println("Entrez map file not found");
        } catch (IOException e) {
            System.err.println("Error reading entrez map file");
        }
        return map;
    }

}
