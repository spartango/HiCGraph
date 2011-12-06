package com.spartango.hicgraph.data;

import java.util.Vector;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

public class BinaryGraphBuilder implements GraphBuilder {

    private int binSize;

    public BinaryGraphBuilder(int binSize) {
        this.binSize = binSize;
    }

    @Override
    public ChromatinGraph buildChromatinGraph(Vector<HiCRead> reads) {

        ChromatinGraph graph = new ChromatinGraph();

        // For each read
        for (HiCRead read : reads) {
            // Bin the read positions
            long firstReadPosition = bin(read.getFirstPosition());
            long secondReadPosition = bin(read.getSecondPosition());
            // Generate a node for each side (if none exists)
            ChromatinLocation firstLoc = new ChromatinLocation(
                                                               read.getFirstChromosome(),
                                                               firstReadPosition,
                                                               read.getFirstStrand());
            ChromatinLocation secondLoc = new ChromatinLocation(
                                                                read.getSecondChromosome(),
                                                                secondReadPosition,
                                                                read.getSecondStrand());
            graph.addVertex(firstLoc);
            graph.addVertex(secondLoc);

            // Then link the nodes with an edge.
            ChromatinRelation link = new ChromatinRelation();
            graph.addEdge(link, firstLoc, secondLoc);
        }
        return graph;
    }

    private long bin(long target) {
        // TODO implement binning
        return target;
    }
}
