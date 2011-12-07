package com.spartango.hicgraph.data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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

    public BinaryGraphBuilder(int binSize) {
        this.binSize = binSize;
        graph = new ChromatinGraph();
        consumer = null;

        buildThread = new Thread(this);
        running = false;

        source = null;
        sourceComplete = false;
    }

    private void addRead(HiCRead read) {
        // Bin the read positions
        long firstReadPosition = bin(read.getFirstPosition());
        long secondReadPosition = bin(read.getSecondPosition());
        
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
        ChromatinRelation link = new ChromatinRelation();
        graph.addEdge(link, firstLoc, secondLoc);
    }

    private long bin(long target) {
        // TODO implement binning
        return target;
    }

    @Override
    public void onReadingStarted(BlockingQueue<HiCRead> readQueue) {
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

        while (running) {
            // Try to grab an element off of the sourceQueue
            if (source != null && !sourceComplete) {
                try {
                    HiCRead read = source.poll(120, TimeUnit.MILLISECONDS);

                    if (read != null) {
                        addRead(read);
                    }

                } catch (InterruptedException e) {
                    System.err.println("Interrupted while polling for data");
                }
            } else {
                // No more data here
                running = false;
            }
        }
        
        notifyComplete();

    }

    private void notifyComplete() {
        if(consumer != null) {
            consumer.onGraphBuilt(graph);
        }
    }
}
