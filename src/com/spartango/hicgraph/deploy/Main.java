package com.spartango.hicgraph.deploy;

import com.spartango.ensembl.model.Genome;
import com.spartango.ensembl.raw.EnsemblParser;
import com.spartango.hicgraph.analysis.cluster.Clusterer;
import com.spartango.hicgraph.analysis.cluster.CoefficientClusterer;
import com.spartango.hicgraph.analysis.stat.StatisticGatherer;
import com.spartango.hicgraph.data.CoexpressionGraphBuilder;
import com.spartango.hicgraph.data.GraphBuilder;
import com.spartango.hicgraph.data.gene.GeneBinner;
import com.spartango.hicgraph.data.raw.HiCDataSource;
import com.spartango.hicgraph.data.raw.HiCParser;

public class Main {

    public static final String WORKSPACE_PREFIX = "/Volumes/DarkIron/HiC Data/raw/";
    public static final String DATA_PATH        = WORKSPACE_PREFIX
                                                  + "139140.txt";
    public static final String ENSEMBL_DB_PATH  = WORKSPACE_PREFIX
                                                  + "Homo_sapiens.GRCh37.65.gtf";
    public static final String COEXPRES_DB_PATH = WORKSPACE_PREFIX
                                                  + "Hsa.coex.v6.top3.txt";
    public static final String ID_MAP_PATH      = WORKSPACE_PREFIX
                                                  + "GeneNameEntrez.txt";

    public static void main(String[] args) {
        // Setup data source
        HiCDataSource dataSource = new HiCParser(DATA_PATH);

        Genome humanGenome = EnsemblParser.buildGenome(ENSEMBL_DB_PATH);

        GeneBinner binner = new GeneBinner(50000, humanGenome);
        // HiCDataSource dataSource = new ControlDataSource(21392274);

        // Setup Graph Pipe
        GraphBuilder builder = new CoexpressionGraphBuilder(COEXPRES_DB_PATH,
                                                            ID_MAP_PATH);

        // Clusterer
        Clusterer clusterer = new CoefficientClusterer(.66);

        StatisticGatherer gatherer = new StatisticGatherer();

        // Assemble pipes
        dataSource.addConsumer(binner);
        binner.addConsumer(builder);

        builder.addConsumer(clusterer);
        clusterer.addConsumer(gatherer);

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
