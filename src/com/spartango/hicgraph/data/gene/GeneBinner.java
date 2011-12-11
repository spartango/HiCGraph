package com.spartango.hicgraph.data.gene;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.spartango.ensembl.model.Chromosome;
import com.spartango.ensembl.model.Gene;
import com.spartango.ensembl.model.Genome;
import com.spartango.hicgraph.data.raw.HiCDataConsumer;
import com.spartango.hicgraph.data.raw.HiCDataSource;
import com.spartango.hicgraph.data.raw.HiCRead;

public class GeneBinner implements HiCDataConsumer, Runnable, HiCDataSource {
    public static final int        NUM_THREADS = 1;

    private HiCDataConsumer        consumer;

    private BlockingQueue<HiCRead> source;
    private boolean                sourceComplete;

    private ThreadPoolExecutor     threadPool;
    private boolean                running;

    private boolean                cacheReads;
    private List<HiCRead>          reads;
    LinkedBlockingQueue<HiCRead>   newReads;
    private int                    fallbackBinSize;

    private Genome                 genome;

    public GeneBinner(int fallbackBinSize, Genome g) {
        this(false, fallbackBinSize, g);
    }

    public GeneBinner(boolean cacheReads, int fallbackBinSize, Genome g) {
        this.cacheReads = cacheReads;
        this.fallbackBinSize = fallbackBinSize;

        consumer = null;
        source = null;

        sourceComplete = false;
        running = false;

        threadPool = new ThreadPoolExecutor(NUM_THREADS, NUM_THREADS, 1000,
                                            TimeUnit.MILLISECONDS,
                                            new LinkedBlockingQueue<Runnable>());

        reads = new LinkedList<HiCRead>();
        genome = g;
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
            // init Read Vector
            newReads = new LinkedBlockingQueue<HiCRead>();
            notifyStart(newReads);

            for (int i = 0; i < NUM_THREADS; i++) {
                threadPool.execute(this);
            }
        }
    }

    @Override
    public void stopReading() {
        // Kill any run in progress
        running = false;
        threadPool.shutdownNow();
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
        System.out.println("Binner Started: "
                           + Thread.currentThread().getName());

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

        // Sanity check for real chromosomes:
        if (read == null
            || (read.getFirstChromosome() == 0 || read.getSecondChromosome() == 0)) {
            return null;
        }

        // For each side
        Gene firstPlacedGene = null;
        Gene secondPlacedGene = null;
        // See if there's a gene there
        Chromosome firstChromosome = genome.getChromosome(read.getFirstChromosome());
        firstPlacedGene = firstChromosome.getGeneForPosition((int) read.getFirstPosition());

        Chromosome secondChromosome = genome.getChromosome(read.getSecondChromosome());
        secondPlacedGene = secondChromosome.getGeneForPosition((int) read.getSecondPosition());

        long firstPosition;

        if (firstPlacedGene != null) {
            // System.out.println("Found gene: "
            // + firstPlacedGene.getDescription());
            firstPosition = firstPlacedGene.getStart();
        } else {
            return null;
            // firstPosition = bin(read.getFirstPosition());
        }

        long secondPosition;

        if (secondPlacedGene != null) {
            // System.out.println("Found gene: "
            // + secondPlacedGene.getDescription());
            secondPosition = secondPlacedGene.getStart();
        } else {
            return null;
            // secondPosition = bin(read.getSecondPosition());
        }

        return new HiCRead(read.getName(), read.getFirstChromosome(),
                           firstPosition, read.getFirstStrand(),
                           firstPlacedGene, read.getSecondChromosome(),
                           secondPosition, read.getSecondStrand(),
                           secondPlacedGene);
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

    private long bin(long position) {
        return fallbackBinSize * (position / fallbackBinSize);
    }

}
