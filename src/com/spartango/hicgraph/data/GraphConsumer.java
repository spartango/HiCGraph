package com.spartango.hicgraph.data;

import com.spartango.hicgraph.model.ChromatinGraph;

public interface GraphConsumer {
    public void onGraphBuilt(ChromatinGraph graph);
}
