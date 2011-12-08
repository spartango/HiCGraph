package com.spartango.hicgraph.data;

import com.spartango.hicgraph.data.raw.HiCDataConsumer;
import com.spartango.hicgraph.model.ChromatinGraph;

public interface GraphBuilder extends HiCDataConsumer {
    
    public ChromatinGraph getGraph();
    public void addConsumer(GraphConsumer c);
    public void removeConsumer(GraphConsumer c);
    
}
