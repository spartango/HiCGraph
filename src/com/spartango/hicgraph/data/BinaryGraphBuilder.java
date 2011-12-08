package com.spartango.hicgraph.data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.spartango.hicgraph.data.filters.ReadFilter;
import com.spartango.hicgraph.data.raw.HiCRead;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

public class BinaryGraphBuilder implements GraphBuilder, Runnable {

    private int                    binSize;
    private ChromatinGraph         graph;
    private GraphConsumer          consumer;

    private Thread                 buildThread;
    private boolean                running;

    private BlockingQueue<HiCRead> source;
    private boolean                sourceComplete;

    private ReadFilter             filter;

    public BinaryGraphBuilder(int binSize) {
        this(binSize, null);
    }

    public BinaryGraphBuilder(int binSize, ReadFilter f) {
        this.binSize = binSize;
        graph = new ChromatinGraph();
        consumer = null;

        running = false;

        source = null;
        sourceComplete = false;

        filter = f;
    }

    private void addRead(HiCRead read) {

        // Filter if necessary
        if (filter == null || filter.check(read)) {

            // Bin the read positions
            long firstReadPosition = bin(read.getFirstPosition());
            long secondReadPosition = bin(read.getSecondPosition());

            // Prevent Self links, these are meaningless
            if (firstReadPosition != secondReadPosition) {
                // Generate a node for each side (if none exists)
                ChromatinLocation firstLoc = new ChromatinLocation(
                                                                   read.getFirstChromosome(),
                                                                   firstReadPosition,
                                                                   read.getFirstStrand());
                ChromatinLocation secondLoc = new ChromatinLocation(
                                                                    read.getSecondChromosome(),
                                                                    secondReadPosition,
                                                                    read.getSecondStrand());
                graph.addVertex(firstLoc);
                graph.addVertex(secondLoc);

                // Then link the nodes with an edge.
                ChromatinRelation link = new ChromatinRelation(firstLoc,
                                                               secondLoc);
                if (!graph.containsEdge(link))
                    graph.addEdge(link, firstLoc, secondLoc);
            }
        }
    }

    private long bin(long target) {
        // TODO implement intelligent binning

        // divide by binSize and floor
        return (target / binSize) * binSize;
    }

    @Override
    public void onReadingStarted(BlockingQueue<HiCRead> readQueue) {
        source = readQueue;
        
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
        running = true;
        System.out.println("Builder Started");
        while (running && source != null) {
            // Try to grab an element off of the sourceQueue
            try {
                HiCRead read = source.poll(120, TimeUnit.MILLISECONDS);

                if (read != null) {
                    addRead(read);
                } else if (sourceComplete) {
                    running = false;
                }

            } catch (InterruptedException e) {
                System.err.println("Interrupted while polling for data");
            }

        }
        notifyComplete();
    }

    private void notifyComplete() {
        if (consumer != null) {
            consumer.onGraphBuilt(graph);
        }
    }
}
