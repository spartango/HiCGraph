package com.spartango.hicgraph.analysis;

import java.util.Set;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;

public class GNClusterer extends EdgeBetweennessClusterer implements Clusterer {
    
    public GNClusterer(int splits) {
        super(splits);
    }

    @Override
    public Set<Set<ChromatinLocation>>findClusters(ChromatinGraph graph) {
        return transform(graph);
    }

}
