package com.spartango.hicgraph.data.gene;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.spartango.ensembl.EnsemblConnection;
import com.spartango.hicgraph.data.raw.HiCDataConsumer;
import com.spartango.hicgraph.data.raw.HiCDataSource;
import com.spartango.hicgraph.data.raw.HiCRead;

public class GeneBinner implements HiCDataConsumer, Runnable, HiCDataSource {

    private HiCDataConsumer        consumer;

    private BlockingQueue<HiCRead> source;
    private boolean                sourceComplete;

    private Thread                 buildThread;
    private boolean                running;

    private boolean                cacheReads;
    private List<HiCRead>          reads;

    private int                    fallbackBinSize;
    private EnsemblConnection      ensemblLink;

    public GeneBinner(int fallbackBinSize) {
        this(false, fallbackBinSize);
    }

    public GeneBinner(boolean cacheReads, int fallbackBinSize) {
        this.cacheReads = cacheReads;
        this.fallbackBinSize = fallbackBinSize;

        consumer = null;
        source = null;

        sourceComplete = false;
        running = false;

        reads = new LinkedList<HiCRead>();
        ensemblLink = new EnsemblConnection();

    }

    @Override
    public void addConsumer(HiCDataConsumer l) {
        consumer = l;
    }

    @Override
    public void removeConsumer(HiCDataConsumer l) {
        consumer = null;
    }

    @Override
    public void startReading() {
        if (!running && source != null) {
            reads.clear();

            buildThread = new Thread(this);
            buildThread.start();
        }
    }

    @Override
    public void stopReading() {
        // Kill any run in progress
        running = false;
        if (buildThread != null) {
            buildThread.interrupt();
        }
    }

    @Override
    public List<HiCRead> readAll() {
        startReading();
        while (running) {
            // Wait for reading to finish
            Thread.yield();

        }
        return reads;
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Binner Started");

        // init Read Vector
        LinkedBlockingQueue<HiCRead> newReads = new LinkedBlockingQueue<HiCRead>();
        notifyStart(newReads);

        while (running && source != null) {
            // Try to grab an element off of the sourceQueue
            try {
                HiCRead read = source.poll(120, TimeUnit.MILLISECONDS);
                HiCRead modifiedRead = handleRead(read);

                if (modifiedRead != null) {
                    newReads.put(modifiedRead);
                    if (cacheReads) {
                        reads.add(modifiedRead);
                    }
                    notifyRead(modifiedRead);
                } else if (sourceComplete) {
                    running = false;
                }

            } catch (InterruptedException e) {
                System.err.println("Binner interrupted while polling for data");
            }

        }
        notifyComplete();

    }

    private void notifyComplete() {
        if (consumer != null) {
            consumer.onReadingComplete();
        }

    }

    private void notifyRead(HiCRead modifiedRead) {
        if (consumer != null) {
            consumer.onNewRead(modifiedRead);
        }

    }

    private HiCRead handleRead(HiCRead read) {
        // Given a read 
        
        // For each side
        // See if there's a gene there
        // use that gene's start site
        
        // else bin it with the fallback window
    }

    private void notifyStart(LinkedBlockingQueue<HiCRead> newReads) {
        if (consumer != null) {
            consumer.onReadingStarted(newReads);
        }
    }

    @Override
    public void onReadingStarted(BlockingQueue<HiCRead> readQueue) {
        source = readQueue;
        sourceComplete = false;
        startReading();

    }

    @Override
    public void onReadingComplete() {
        sourceComplete = true;

    }

    @Override
    public void onNewRead(HiCRead newRead) {
    }

}
