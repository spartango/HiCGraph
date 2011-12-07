package com.spartango.hicgraph.analysis;

import java.util.Set;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;

public interface Clusterer {
    public Set<Set<ChromatinLocation>> findClusters(ChromatinGraph graph);
    
}
