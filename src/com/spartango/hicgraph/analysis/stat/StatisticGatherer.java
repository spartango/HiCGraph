package com.spartango.hicgraph.analysis.stat;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.spartango.hicgraph.analysis.cluster.ClusterConsumer;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;

import edu.uci.ics.jung.algorithms.metrics.Metrics;

public class StatisticGatherer implements ClusterConsumer, Runnable {

    private ChromatinGraph                mainGraph;
    private BlockingQueue<ChromatinGraph> clusterSource;

    private boolean                       running;
    private boolean                       sourceComplete;

    private Thread                        statThread;
    private String                        filePath;

    public StatisticGatherer(String filePath) {
        running = sourceComplete = false;
        this.filePath = filePath;
    }

    @Override
    public void onClusteringStarted(ChromatinGraph source,
                                    BlockingQueue<ChromatinGraph> clusterQueue) {
        mainGraph = source;
        clusterSource = clusterQueue;

        statThread = new Thread(this);
        statThread.start();
    }

    @Override
    public void onClusterFound(ChromatinGraph cluster) {
    }

    @Override
    public void onClusteringComplete() {
        sourceComplete = true;
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Statistics Started on "
                           + Thread.currentThread().getName());

        // Gather Global stats
        saveGlobalStats(mainGraph);
        int clusters = 0;
        while (running && clusterSource != null) {
            // Try to grab an element off of the sourceQueue
            try {
                ChromatinGraph cluster = clusterSource.poll(120,
                                                            TimeUnit.MILLISECONDS);

                if (cluster != null) {
                    // Print Cluster Stats
                    saveClusterStats(cluster);
                    System.out.print("Clusters found: "+clusters+"\r");
                    clusters++;
                } else if (sourceComplete) {
                    running = false;
                }

            } catch (InterruptedException e) {
                System.err.println("Statistics interrupted while polling for data");
            }

        }
        System.out.println("\nStatistics Finished: " + clusters + " clusters");

        // Last pipe
        System.exit(0);
    }

    private void saveGlobalStats(ChromatinGraph mainGraph2) {
        FileWriter writer;
        try {
            writer = new FileWriter(this.filePath + "global_stats");
            writer.write(generateGlobalStats(mainGraph2));
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing global stats to file");
        }

    }

    private void saveClusterStats(ChromatinGraph cluster) {
        FileWriter writer;
        try {
            writer = new FileWriter(this.filePath + "cluster_"
                                    + cluster.hashCode());
            writer.write(generateClusterStats(cluster));
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing global stats to file");
        }
    }

    private String generateClusterStats(ChromatinGraph cluster) {
        String stats = "";
        stats += "Nodes: " + cluster.getVertexCount() + "\n";
        stats += "Edges: " + cluster.getEdgeCount() + "\n";
        stats += "Contents: " + cluster.getVertices()+"\n";
        return stats;
    }

    private String generateGlobalStats(ChromatinGraph mainGraph2) {
        String stats = "";

        SortedMap<Integer, Integer> edgeDistribution = calculateEdgeDistribution(mainGraph2);
        for (Map.Entry<Integer, Integer> entry : edgeDistribution.entrySet()) {
            stats += entry.getKey() + "\t" + entry.getValue() + "\n";
        }
        // Calculate the cardinality distribution

        return stats;
    }

    private SortedMap<Integer, Integer> calculateEdgeDistribution(ChromatinGraph mainGraph2) {
        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
        for (ChromatinLocation loc : mainGraph2.getVertices()) {
            int edges = mainGraph2.degree(loc);
            if (map.containsKey(edges)) {
                map.put(edges, map.get(edges) + 1);
            } else {
                map.put(edges, 1);
            }
        }
        return map;
    }
}
