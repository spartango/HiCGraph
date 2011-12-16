package com.spartango.hicgraph.analysis.cluster;

import com.spartango.hicgraph.graph.GraphConsumer;

public interface Clusterer extends GraphConsumer {

    public void addConsumer(ClusterConsumer c);

    public void removeConsumer(ClusterConsumer c);
}
