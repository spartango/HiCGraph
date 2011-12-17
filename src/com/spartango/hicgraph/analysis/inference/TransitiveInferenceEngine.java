package com.spartango.hicgraph.analysis.inference;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import com.spartango.hicgraph.data.filters.ReadFilter;
import com.spartango.hicgraph.data.raw.HiCRead;
import com.spartango.hicgraph.graph.GraphBuilder;
import com.spartango.hicgraph.graph.GraphConsumer;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

public class TransitiveInferenceEngine implements Runnable, GraphConsumer,
                                      GraphBuilder {

    private GraphConsumer  consumer;
    private ChromatinGraph source;

    private Thread         inferenceThread;
    private double         certaintyThreshold;

    public TransitiveInferenceEngine(double d) {
        certaintyThreshold = d;
    }

    @Override
    public void onGraphBuilt(ChromatinGraph graph) {
        source = graph;
        inferenceThread = new Thread(this);
        inferenceThread.start();

    }

    @Override
    public void run() {
        int inferredCount = 0;

        // for each node
        System.out.println("Inferring Edges on "
                           + Thread.currentThread().getName() + ": "
                           + source.getEdgeCount() + " edges & "
                           + source.getVertexCount() + " nodes");
        for (ChromatinLocation node : source.getVertices()) {

            // Get neighbors
            ChromatinLocation[] neighbors = source.getNeighbors(node)
                                                  .toArray(new ChromatinLocation[0]);
            Collection<ChromatinRelation> neighborEdges = source.getIncidentEdges(node);

            // For each pair of neighbors, ensure that they are connected
            for (int i = 0; i < neighbors.length; i++) {
                for (int j = i + 1; j < neighbors.length; j++) {
                    // Then link the nodes with an edge.
                    ChromatinRelation link = new ChromatinRelation(
                                                                   neighbors[i],
                                                                   neighbors[j]);

                    // This will be used to verify that redundancy is avoided
                    ChromatinRelation revLink = new ChromatinRelation(
                                                                      neighbors[j],
                                                                      neighbors[i]);
                    if (!source.containsEdge(link)
                        && !source.containsEdge(revLink)) {

                        // Find the neighbor edges for each element
                        ChromatinRelation iRelation = findEdge(neighbors[i],
                                                               neighborEdges);
                        ChromatinRelation jRelation = findEdge(neighbors[j],
                                                               neighborEdges);

                        // If they arent connected, add a new edge, but with
                        // reduced
                        // certainty
                        // Should probably not connect things with too low
                        // certainty
                        if (jRelation != null && iRelation != null) {
                            double newCertainty = (iRelation.getCertainty() + jRelation.getCertainty()) / 4.0;
                            if (newCertainty >= certaintyThreshold) {
                                link.setCertainty(newCertainty);
                                source.addEdge(link, neighbors[i], neighbors[j]);
                                inferredCount++;
                                if (inferredCount % 100 == 0)
                                    System.out.print("Edges inferred: "
                                                     + inferredCount + "\r");

                            }
                        }
                    }
                }
            }

        }
        System.out.println("Inferred Edges " + inferredCount + " => "
                           + source.getEdgeCount());
        notifyComplete(source);
    }

    private void notifyComplete(ChromatinGraph graph) {
        if (consumer != null) {
            consumer.onGraphBuilt(graph);
        }
    }

    @Override
    public void onReadingStarted(BlockingQueue<HiCRead> readQueue) {
        System.err.println(this + ": HiCData input not supported");
    }

    @Override
    public void onReadingComplete() {
    }

    @Override
    public void onNewRead(HiCRead newRead) {
    }

    @Override
    public ChromatinGraph getGraph() {
        return source;
    }

    @Override
    public void addConsumer(GraphConsumer c) {
        consumer = c;
    }

    @Override
    public void removeConsumer(GraphConsumer c) {
        consumer = null;
    }

    @Override
    public void addFilter(ReadFilter f) {
        System.err.println(this + ": Filter not supported");
    }

    @Override
    public void removeFilter(ReadFilter r) {
    }

    private static ChromatinRelation findEdge(ChromatinLocation target,
                                              Collection<ChromatinRelation> edges) {
        for (ChromatinRelation rel : edges) {
            if (rel.getFirstLoc() == target || rel.getSecondLoc() == target) {
                return rel;
            }
        }
        return null;
    }
}
