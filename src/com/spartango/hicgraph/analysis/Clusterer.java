package com.spartango.hicgraph.analysis;

import java.util.Set;

import com.spartango.hicgraph.data.GraphConsumer;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;

public interface Clusterer extends GraphConsumer {

    public void addConsumer(ClusterConsumer c);

    public void removeConsumer(ClusterConsumer c);

    public Set<Set<ChromatinLocation>> findClusters(ChromatinGraph graph);

}
