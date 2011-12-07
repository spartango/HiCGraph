package com.spartango.hicgraph.analysis;

import java.util.Map;
import java.util.Set;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;

import edu.uci.ics.jung.algorithms.metrics.Metrics;

public class CoefficientClusterer implements Clusterer {
    
    private double threshold;
    
    public CoefficientClusterer(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public Set<Set<ChromatinLocation>> findClusters(ChromatinGraph graph) {
        // Get clustering coefficients
        Map<ChromatinLocation, Double> coefficients = Metrics.clusteringCoefficients(graph);
        
        // Filter based on threshold & distribution
        
        // Pull up neighbors as "cluster"
        
        // TODO generate clusters from coefficient
        return null;
    }

}
