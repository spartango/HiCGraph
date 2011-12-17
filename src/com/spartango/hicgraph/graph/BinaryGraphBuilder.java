package com.spartango.hicgraph.graph;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.spartango.hicgraph.data.filters.ReadFilter;
import com.spartango.hicgraph.data.raw.HiCRead;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

public class BinaryGraphBuilder implements GraphBuilder, Runnable {

    private ChromatinGraph             graph;
    private GraphConsumer              consumer;

    private Thread                     buildThread;
    private boolean                    running;

    private BlockingQueue<HiCRead>     source;
    private boolean                    sourceComplete;

    private Vector<ReadFilter>         filters;
    private Vector<RelationDataSource> dataSources;

    public BinaryGraphBuilder() {
        graph = new ChromatinGraph();
        consumer = null;

        running = false;

        source = null;
        sourceComplete = false;

        dataSources = new Vector<RelationDataSource>();

        filters = new Vector<ReadFilter>();
    }

    private void addRead(HiCRead read) {

        // Filter if necessary
        if (checkFilters(read)) {

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

                // This will be used to verify that redundancy is avoided
                ChromatinRelation revLink = new ChromatinRelation(secondLoc,
                                                                  firstLoc);

                for (RelationDataSource source : dataSources) {
                    source.applyData(link);
                }

                if (!graph.containsEdge(link) && !graph.containsEdge(revLink)) {
                    graph.addEdge(link, firstLoc, secondLoc);
                    if (graph.getEdgeCount() % 100 == 0)
                        System.out.print("Edges: " + graph.getEdgeCount()
                                         + "\r");
                }
            }
        }
    }

    private boolean checkFilters(HiCRead read) {
        for (ReadFilter filter : filters) {
            if (!filter.check(read))
                return false;
        }
        return true;
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
        System.out.println("Builder Started on "
                           + Thread.currentThread().getName() + " with "
                           + filters.size() + " filter(s)");
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
        System.out.println("\nBuilder Finished: " + reads + " reads");
        notifyComplete();
    }

    private void notifyComplete() {
        if (consumer != null) {
            consumer.onGraphBuilt(graph);
        }
    }

    public void addDataSource(RelationDataSource r) {
        dataSources.add(r);
    }

    public void removeDataSource(RelationDataSource r) {
        dataSources.remove(r);
    }

    public void addFilter(ReadFilter f) {
        filters.add(f);
    }

    public void removeFilter(ReadFilter f) {
        filters.remove(f);
    }

}
