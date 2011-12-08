package com.spartango.hicgraph.analysis;

import java.util.Set;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;

public class GNClusterer extends
        EdgeBetweennessClusterer<ChromatinLocation, ChromatinRelation>
                                                                      implements
                                                                      Clusterer,
                                                                      Runnable {

    private ChromatinGraph  source;

    private ClusterConsumer consumer;

    private boolean         running;
    private Thread          clusterThread;

    public GNClusterer(int splits) {
        super(splits);
        consumer = null;
        source = null;
        running = false;
    }

    @Override
    public Set<Set<ChromatinLocation>> findClusters(ChromatinGraph graph) {
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
    public void run() {
        if (source != null) {
            running = true;

            Set<Set<ChromatinLocation>> clusters = findClusters(source);
            notifyComplete(clusters);

            running = false;
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

    private void notifyComplete(Set<Set<ChromatinLocation>> clusters) {
        if (consumer != null) {
            consumer.onClusteringComplete(clusters);
        }
    }
}
