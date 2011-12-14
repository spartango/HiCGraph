package com.spartango.hicgraph.analysis.cluster;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;

import edu.uci.ics.jung.algorithms.metrics.Metrics;

public class CoefficientClusterer implements Clusterer, Runnable {

    private double          threshold;
    private ChromatinGraph  source;

    private ClusterConsumer consumer;

    private boolean         running;
    private Thread          clusterThread;

    public CoefficientClusterer(double threshold) {
        this.threshold = threshold;
        consumer = null;
        source = null;
        running = false;
    }

    @Override
    public Set<Set<ChromatinLocation>> findClusters(ChromatinGraph graph) {
        // Get clustering coefficients
        Map<ChromatinLocation, Double> coefficients = Metrics.clusteringCoefficients(graph);

        List<ChromatinLocation> thresholded = new LinkedList<ChromatinLocation>();

        // Pull up neighbors as "cluster"
        Set<Set<ChromatinLocation>> clusters = new HashSet<Set<ChromatinLocation>>();

        // Filter based on threshold & distribution
        for (ChromatinLocation key : coefficients.keySet()) {
            if (coefficients.get(key) >= threshold) {
                thresholded.add(key);

                Set<ChromatinLocation> cluster = new HashSet<ChromatinLocation>();
                cluster.add(key);

                Collection<ChromatinLocation> neighbors = graph.getNeighbors(key);
                cluster.addAll(neighbors);

                for (ChromatinLocation loc : neighbors) {
                    cluster.addAll(graph.getNeighbors(loc));
                }

                System.out.println("ClusterHead: " + coefficients.get(key)
                                   + " -> " + key + " => "
                                   + graph.getNeighbors(key));
                clusters.add(cluster);
            }
        }

        return clusters;
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
