package com.spartango.hicgraph.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class HiCParser implements HiCDataSource {
    public static int BUFFER_SIZE = 1000000;

    private String    sourceFile;

    public HiCParser(String filename) {
        sourceFile = filename;
    }

    @Override
    public Vector<HiCRead> generateReads() {
        // Open file for read
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(sourceFile));
        } catch (FileNotFoundException e1) {
            System.err.println("Failed to find source file: " + sourceFile);
        }

        // init Read Vector
        Vector<HiCRead> newReads = new Vector<HiCRead>(BUFFER_SIZE);

        // Read each line
        String line = null;
        try {
            while (reader != null && (line = reader.readLine()) != null) {

                // Parse Line into HiCRead, save it
                HiCRead read = parseLine(line);
                if (read != null) {
                    newReads.add(read);
                }

            }
        } catch (IOException e) {
            System.err.println("Got stuck while trying to read file: @line "
                               + newReads.size());
        }

        return newReads;
    }

    public static HiCRead parseLine(String line) {
        // Data format:
        // read name, chromosome1, position1, strand1, restrictionfragment1,
        // chromosome2, position2, strand2, restrictionfragment2 \n
        // Screen against comments
        if (!line.startsWith("#")) {
            String[] parts = line.split(" ");
            if (parts.length == 9) {
                try {
                    String name = parts[0];
                    int chromosome1 = Integer.parseInt(parts[1]);
                    long position1 = Long.parseLong(parts[2]);
                    int strand1 = Integer.parseInt(parts[3]);
                    int fragment1 = Integer.parseInt(parts[4]);
                    int chromosome2 = Integer.parseInt(parts[5]);
                    long position2 = Long.parseLong(parts[6]);
                    int strand2 = Integer.parseInt(parts[7]);
                    int fragment2 = Integer.parseInt(parts[8]);

                    HiCRead newRead = new HiCRead(name, chromosome1, position1,
                                                  strand1, fragment1,
                                                  chromosome2, position2,
                                                  strand2, fragment2);
                    return newRead;

                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse line " + line
                                       + " expected a number here");
                }

            } else {
                System.err.println("Failed to parse line " + line
                                   + " incorrect column number");
            }
        }
        // Failed to parse this or this was a comment. nothing to return
        return null;
    }
}
