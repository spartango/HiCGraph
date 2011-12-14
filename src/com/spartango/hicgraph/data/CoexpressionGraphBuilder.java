package com.spartango.hicgraph.data;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.spartango.coexpres.CoexpresDbParser;
import com.spartango.hicgraph.data.filters.ReadFilter;
import com.spartango.hicgraph.data.raw.HiCRead;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

import edu.uci.ics.jung.graph.util.Pair;

public class CoexpressionGraphBuilder implements GraphBuilder, Runnable {

    private ChromatinGraph                graph;
    private GraphConsumer                 consumer;

    private Thread                        buildThread;
    private boolean                       running;

    private BlockingQueue<HiCRead>        source;
    private boolean                       sourceComplete;

    private HashMap<Pair<String>, Double> coexpressionMap;

    private ReadFilter                    filter;

    public CoexpressionGraphBuilder(String coexpresdbFile, String idmapFile) {
        this(null, coexpresdbFile, idmapFile);
    }

    public CoexpressionGraphBuilder(ReadFilter f,
                                    String coexpresdbFile,
                                    String idmapFile) {
        graph = new ChromatinGraph();
        consumer = null;

        running = false;

        source = null;
        sourceComplete = false;

        coexpressionMap = CoexpresDbParser.parseDb(coexpresdbFile, idmapFile);

        filter = f;
    }

    private void addRead(HiCRead read) {

        // Filter if necessary
        if (filter == null || filter.check(read)) {

            // Prevent Self links, these are meaningless
            if (read.getFirstPosition() != read.getSecondPosition()) {
                // Generate a node for each side (if none exists)
                ChromatinLocation firstLoc = new ChromatinLocation(
                                                                   read.getFirstChromosome(),
                                                                   read.getFirstPosition(),
                                                                   read.getFirstStrand(),
                                                                   read.getFirstGene());
                ChromatinLocation secondLoc = new ChromatinLocation(
                                                                    read.getSecondChromosome(),
                                                                    read.getSecondPosition(),
                                                                    read.getSecondStrand(),
                                                                    read.getSecondGene());
                graph.addVertex(firstLoc);
                graph.addVertex(secondLoc);

                // Then link the nodes with an edge.
                ChromatinRelation link = new ChromatinRelation(firstLoc,
                                                               secondLoc);
                String firstGeneName = firstLoc.getGene() != null ? firstLoc.getGene()
                                                                            .getName()
                                                                 : "";
                String secondGeneName = firstLoc.getGene() != null ? secondLoc.getGene()
                                                                              .getName()
                                                                  : "";

                Double coexpression = coexpressionMap.get(new Pair<String>(
                                                                           firstGeneName,
                                                                           secondGeneName));
                if (coexpression == null) {
                    coexpression = coexpressionMap.get(new Pair<String>(
                                                                        secondGeneName,
                                                                        firstGeneName));
                }

                link.setCoexpressionCorrelation(coexpression != null ? coexpression
                                                                    : 0.0);

                if (!graph.containsEdge(link))
                    graph.addEdge(link, firstLoc, secondLoc);
            }
        }
    }

    @Override
    public void onReadingStarted(BlockingQueue<HiCRead> readQueue) {
        source = readQueue;
        sourceComplete = false;

        buildThread = new Thread(this);

        // Start the build thread
        buildThread.start();
    }

    @Override
    public void onReadingComplete() {
        // Notify the builder
        sourceComplete = true;
    }

    @Override
    public void onNewRead(HiCRead newRead) {
    }

    @Override
    public ChromatinGraph getGraph() {
        return graph;
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
    public void run() {
        int reads = 0;
        running = true;
        System.out.println("Builder Started: "
                           + Thread.currentThread().getName());
        while (running && source != null) {
            // Try to grab an element off of the sourceQueue
            try {
                HiCRead read = source.poll(120, TimeUnit.MILLISECONDS);

                if (read != null) {
                    addRead(read);
                    reads++;
                } else if (sourceComplete) {
                    running = false;
                }

            } catch (InterruptedException e) {
                System.err.println("Builder interrupted while polling for data");
            }

        }
        System.out.println("Builder Finished: " + reads + " reads");
        notifyComplete();
    }

    private void notifyComplete() {
        if (consumer != null) {
            consumer.onGraphBuilt(graph);
        }
    }
}
