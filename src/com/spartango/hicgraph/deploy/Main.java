package com.spartango.hicgraph.deploy;

import java.util.Set;

import com.spartango.ensembl.model.Genome;
import com.spartango.ensembl.raw.EnsemblParser;
import com.spartango.hicgraph.analysis.BicomponentClusterer;
import com.spartango.hicgraph.analysis.ClusterConsumer;
import com.spartango.hicgraph.analysis.Clusterer;
import com.spartango.hicgraph.analysis.WeakClusterer;
import com.spartango.hicgraph.data.BinaryGraphBuilder;
import com.spartango.hicgraph.data.filters.IntrachromosomalFilter;
import com.spartango.hicgraph.data.gene.GeneBinner;
import com.spartango.hicgraph.data.raw.ControlDataSource;
import com.spartango.hicgraph.data.raw.HiCDataSource;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.visualization.GraphImageRenderer;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;

public class Main {

    public static void main(String[] args) {
        // Setup data source
        //HiCDataSource dataSource = new HiCParser(
        //                                         "/Volumes/DarkIron/HiC Data/raw/139140.txt");

        Genome humanGenome = EnsemblParser.buildGenome("/Volumes/DarkIron/HiC Data/raw/Homo_sapiens.GRCh37.65.gtf");

        GeneBinner binner = new GeneBinner(50000, humanGenome);
        HiCDataSource dataSource = new ControlDataSource(21392274);

        // Setup Graph Pipe
        BinaryGraphBuilder builder = new BinaryGraphBuilder(
                                                            new IntrachromosomalFilter());

        // Clusterer
        Clusterer clusterer = new BicomponentClusterer();

        // Assemble pipes
        dataSource.addConsumer(binner);
        binner.addConsumer(builder);
        /*
         * builder.addConsumer(new GraphConsumer() {
         * 
         * @Override public void onGraphBuilt(ChromatinGraph graph) { // for
         * (ChromatinLocation l : graph.getVertices()) //
         * System.out.println("> " + l);
         * System.out.println(graph.getVertexCount() + " verticies & " +
         * graph.getEdgeCount() + " edges"); GraphImageRenderer renderer = new
         * GraphImageRenderer(graph, 8128);
         * renderer.saveImage("/Volumes/DarkIron/HiC Data/genes_140.png");
         * System.exit(0); } });
         */

        builder.addConsumer(clusterer);

        clusterer.addConsumer(new ClusterConsumer() {

            @Override
            public void onClusteringComplete(Set<Set<ChromatinLocation>> clusters,
                                             ChromatinGraph graph) {
                for (Set<ChromatinLocation> cluster : clusters) {
                    if (cluster.size() > 5) {
                        System.out.println(cluster);
                        GraphImageRenderer renderer = new GraphImageRenderer(
                                                                             FilterUtils.createInducedSubgraph(cluster,
                                                                                                               graph),
                                                                             8128);
                        renderer.saveImage("/Volumes/DarkIron/HiC Data/images/bc/control_"
                                           + cluster.hashCode() + ".png");
                    }
                }

                System.out.println(clusters.size() + " clusters found");
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
