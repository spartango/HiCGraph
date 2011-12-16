package com.spartango.hicgraph.data.raw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.spartango.hicgraph.model.ChromatinCompartment;

public class HiCCompartmentMap {

    private TreeMap<Long, ChromatinCompartment> map;

    public HiCCompartmentMap() {
        map = new TreeMap<Long, ChromatinCompartment>();
    }

    public void insertCompartment(ChromatinCompartment c) {
        map.put(c.getStart(), c);
    }

    public ChromatinCompartment getComparment(long position) {
        Entry<Long, ChromatinCompartment> candidate = map.floorEntry(position);
        if (candidate != null && candidate.getValue().getEnd() >= position) {
            return candidate.getValue();
        } else
            return null;
    }

    public int size() {
        return map.size();
    }

    public static HiCCompartmentMap parseFromFile(String filename,
                                                  int resolution) {
        HiCCompartmentMap newMap = new HiCCompartmentMap();
        System.out.println("Building Compartment Map for " + filename);
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            long currentPosition = 0;
            ChromatinCompartment currentCompartment = null;
            DecimalFormat format = new DecimalFormat();
            while ((line = reader.readLine()) != null) {
                // Each line is a bin
                double compartValue = format.parse(line.trim().replace("+", ""))
                                            .doubleValue();
                int state = (compartValue > 0 ? ChromatinCompartment.OPEN
                                             : ChromatinCompartment.CLOSED);

                if (currentCompartment == null
                    || currentCompartment.getState() != state) {
                    currentCompartment = new ChromatinCompartment(
                                                                  currentPosition,
                                                                  currentPosition
                                                                          + resolution,
                                                                  state);
                    newMap.insertCompartment(currentCompartment);

                } else if (currentCompartment.getState() == state) {
                    currentCompartment.setEnd(currentPosition + resolution);
                }
                currentPosition += resolution;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Failed to find " + filename
                               + " to generate Compartments");
        } catch (IOException e) {
            System.err.println("Error reading " + filename
                               + " when generating Compartments");
        } catch (ParseException e) {
            System.err.println("Error reading " + filename
                               + ", couldn't parse a number from " + line);
        }
        System.out.println("Built Compartment Map " + newMap.size());
        return newMap;
    }
}
