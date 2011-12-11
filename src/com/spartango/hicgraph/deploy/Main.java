package com.spartango.hicgraph.deploy;

import com.spartango.ensembl.model.Genome;
import com.spartango.ensembl.raw.EnsemblParser;
import com.spartango.hicgraph.data.BinaryGraphBuilder;
import com.spartango.hicgraph.data.GraphConsumer;
import com.spartango.hicgraph.data.gene.GeneBinner;
import com.spartango.hicgraph.data.raw.HiCDataSource;
import com.spartango.hicgraph.data.raw.HiCParser;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.visualization.GraphImageRenderer;

public class Main {

    public static void main(String[] args) {
        // Setup data source
        HiCDataSource dataSource = new HiCParser(
                                                 "/Volumes/DarkIron/HiC Data/raw/GSM455140_428EGAAXX.8.maq.hic.summary.binned.txt");

        Genome humanGenome = EnsemblParser.buildGenome("/Volumes/DarkIron/HiC Data/raw/Homo_sapiens.GRCh37.65.gtf");
        
        GeneBinner binner = new GeneBinner(2000, humanGenome);
        // HiCDataSource dataSource = new ControlDataSource(2000, 1);

        // Setup Graph Pipe
        BinaryGraphBuilder builder = new BinaryGraphBuilder();

        // Clusterer
        // Clusterer clusterer = new GNClusterer(30);

        // Assemble pipes
        dataSource.addConsumer(binner);
        binner.addConsumer(builder);
        builder.addConsumer(new GraphConsumer() {

            @Override
            public void onGraphBuilt(ChromatinGraph graph) {
                for (ChromatinLocation l : graph.getVertices())
                    System.out.println("> " + l);
                
                GraphImageRenderer renderer = new GraphImageRenderer(graph,
                                                                     8128);
                renderer.saveImage("/Volumes/DarkIron/HiC Data/genes_140.png");
                System.exit(0);
            }
        });

        /*
         * builder.addConsumer(clusterer);
         * 
         * clusterer.addConsumer(new ClusterConsumer() {
         * 
         * @Override public void
         * onClusteringComplete(Set<Set<ChromatinLocation>> clusters) {
         * System.out.println(clusters.size() + " clusters found");
         * 
         * for (Set<ChromatinLocation> cluster : clusters) {
         * System.out.println("---\n" + cluster + "\n---\n"); } System.exit(0);
         * } });
         */

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
