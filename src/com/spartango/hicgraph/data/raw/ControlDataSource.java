package com.spartango.hicgraph.data.raw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.spartango.hicgraph.model.ChromatinLocation;

public class ControlDataSource implements HiCDataSource, Runnable {
    private HiCDataConsumer consumer;

    private boolean         reading;
    private Thread          readerThread;
    private List<HiCRead>   reads;

    private int             readsToGenerate;
    private int             chromosomeRestriction;

    public ControlDataSource(int readsToGenerate) {
        this(readsToGenerate, -1);
    }

    public ControlDataSource(int readsToGenerate, int chromosomeRestriction) {
        this.readsToGenerate = readsToGenerate;
        this.chromosomeRestriction = chromosomeRestriction;

        reads = new ArrayList<HiCRead>(readsToGenerate);
    }

    @Override
    public void addConsumer(HiCDataConsumer l) {
        this.consumer = l;
    }

    @Override
    public void removeConsumer(HiCDataConsumer l) {
        this.consumer = null;
    }

    @Override
    public void startReading() {
        if (!reading) {
            reading = true;

            reads.clear();

            readerThread = new Thread(this);

            readerThread.start();
        }
    }

    public void run() {
        System.out.println("Control Data Generator Started");

        reading = true;

        // init Read Vector
        LinkedBlockingQueue<HiCRead> newReads = new LinkedBlockingQueue<HiCRead>();
        notifyStart(newReads);

        // generate each read
        try {
            for (int i = 0; i < readsToGenerate; i++) {

                HiCRead read = generateRead();
                if (read != null) {
                    newReads.put(read);
                    reads.add(read);
                    notifyRead(read);
                }

            }
        } catch (InterruptedException e) {
            System.err.println("Interrupted while putting new read into queue");
        }

        reading = false;
        notifyComplete();
    }

    private HiCRead generateRead() {
        // Generate a read with some randomized data
        String name = "" + Math.random();

        int firstChromosome = (chromosomeRestriction == -1 ? (int) Math.round(Math.random()
                                                                              * (ChromatinLocation.NUM_CHROMOSOMES - 1))
                                                          : chromosomeRestriction);
        long firstPosition = Math.round(ChromatinLocation.CHROMOSOME_LENGTHS[firstChromosome]
                                        * Math.random());
        int firstStrand = 0;
        int secondChromosome = (chromosomeRestriction == -1 ? (int) Math.round(Math.random()
                                                                               * (ChromatinLocation.NUM_CHROMOSOMES - 1))
                                                           : chromosomeRestriction);
        long secondPosition = Math.round(ChromatinLocation.CHROMOSOME_LENGTHS[secondChromosome]
                                         * Math.random());
        int secondStrand = 0;
//        System.out.println("Generated Read: " + firstChromosome + ":"
//                           + firstPosition + " -> " + secondChromosome + ":"
//                           + secondPosition);
        HiCRead newRead = new HiCRead(name, firstChromosome, firstPosition,
                                      firstStrand, secondChromosome,
                                      secondPosition, secondStrand);

        return newRead;
    }

    private void notifyComplete() {
        if (consumer != null) {
            consumer.onReadingComplete();
        }

    }

    private void notifyRead(HiCRead read) {
        if (consumer != null) {
            consumer.onNewRead(read);
        }

    }

    private void notifyStart(LinkedBlockingQueue<HiCRead> newReads) {
        if (consumer != null) {
            consumer.onReadingStarted(newReads);
        }
    }

    @Override
    public void stopReading() {
        reading = false;
        if (readerThread != null) {
            readerThread.interrupt();
        }
    }

    @Override
    public List<HiCRead> readAll() {
        startReading();
        while (reading) {
            // Wait for reading to finish
            Thread.yield();

        }
        return reads;
    }
}
