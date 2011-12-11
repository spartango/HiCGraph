package com.spartango.hicgraph.analysis;

import java.util.Set;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

public class WeakClusterer
        extends
        edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer<ChromatinLocation, ChromatinRelation>
                                                                                                        implements
                                                                                                        Clusterer,
                                                                                                        Runnable {

    private ChromatinGraph  source;

    private ClusterConsumer consumer;

    private boolean         running;
    private Thread          clusterThread;

    public WeakClusterer() {
        consumer = null;
        source = null;
        running = false;
    }

    @Override
    public Set<Set<ChromatinLocation>> findClusters(ChromatinGraph graph) {
        // Get clustering coefficients
        return transform(graph);
    }

    @Override
    public void onGraphBuilt(ChromatinGraph graph) {
        // Single entity in pipeline
        if (!running) {

            // Got a graph to cluster
            source = graph;

            // Spin up a thread to cluster
            clusterThread = new Thread(this);
            clusterThread.start();
        }
    }

    @Override
    public void addConsumer(ClusterConsumer c) {
        consumer = c;
    }

    @Override
    public void removeConsumer(ClusterConsumer c) {
        consumer = null;
    }

    @Override
    public void run() {
        if (source != null) {
            running = true;
            System.out.println("Clusterer Started: " + source.getVertexCount()
                               + " nodes & " + source.getEdgeCount() + " edges");

            Set<Set<ChromatinLocation>> clusters = findClusters(source);
            notifyComplete(clusters, source);

            running = false;
        }
    }

    private void notifyComplete(Set<Set<ChromatinLocation>> clusters,
                                ChromatinGraph graph) {
        if (consumer != null) {
            consumer.onClusteringComplete(clusters, graph);
        }
    }

}
