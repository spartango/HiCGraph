package com.spartango.hicgraph.deploy;

import java.util.Set;

import com.spartango.hicgraph.analysis.ClusterConsumer;
import com.spartango.hicgraph.analysis.Clusterer;
import com.spartango.hicgraph.analysis.GNClusterer;
import com.spartango.hicgraph.data.BinaryGraphBuilder;
import com.spartango.hicgraph.data.filters.IntrachromosomalFilter;
import com.spartango.hicgraph.data.raw.HiCDataSource;
import com.spartango.hicgraph.data.raw.HiCParser;
import com.spartango.hicgraph.model.ChromatinLocation;

public class Main {

    public static void main(String[] args) {
        // Setup data source
        HiCDataSource dataSource = new HiCParser(
                                                 "/Volumes/DarkIron/HiC Data/short_data.txt");
        // HiCDataSource dataSource = new ControlDataSource(1350, 1);

        // Setup Graph Pipe
        BinaryGraphBuilder builder = new BinaryGraphBuilder(
                                                            50000,
                                                            new IntrachromosomalFilter(
                                                                                       1));

        // Clusterer
        Clusterer clusterer = new GNClusterer(300);

        // Assemble pipes
        dataSource.addConsumer(builder);
        builder.addConsumer(clusterer);

        clusterer.addConsumer(new ClusterConsumer() {

            @Override
            public void onClusteringComplete(Set<Set<ChromatinLocation>> clusters) {
                System.out.println(clusters.size() + " clusters found");

                for (Set<ChromatinLocation> cluster : clusters) {
                    System.out.println("---\n" + cluster + "\n---\n");
                }
                System.exit(0);
            }
        });

        // Start pipes
        dataSource.startReading();

        // Let them take care of themselves
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
