package com.spartango.hicgraph.analysis.cluster;

import java.util.Set;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;

public interface ClusterConsumer {

    public void onClusteringComplete(Set<Set<ChromatinLocation>> clusters,
                                     ChromatinGraph graph);

}
