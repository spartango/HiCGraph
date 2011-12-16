package com.spartango.hicgraph.graph;

import com.spartango.hicgraph.model.ChromatinGraph;

public interface GraphConsumer {
    public void onGraphBuilt(ChromatinGraph graph);
}
