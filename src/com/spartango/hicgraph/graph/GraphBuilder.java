package com.spartango.hicgraph.graph;

import com.spartango.hicgraph.data.filters.ReadFilter;
import com.spartango.hicgraph.data.raw.HiCDataConsumer;
import com.spartango.hicgraph.model.ChromatinGraph;

public interface GraphBuilder extends HiCDataConsumer {

    public ChromatinGraph getGraph();

    public void addConsumer(GraphConsumer c);

    public void removeConsumer(GraphConsumer c);

    public void addFilter(ReadFilter f);

    public void removeFilter(ReadFilter r);

}
