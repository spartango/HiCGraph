package com.spartango.hicgraph.data.raw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


public class HiCParser implements HiCDataSource, Runnable {

    private String          sourceFile;
    private HiCDataConsumer consumer;

    private boolean         reading;
    private Thread          readerThread;
    private List<HiCRead>   reads;

    public HiCParser(String filename) {
        sourceFile = filename;
        reading = false;
        consumer = null;
        reads = new LinkedList<HiCRead>();
    }

    public static HiCRead parseLine(String line) {
        // Data format:
        // read name, chromosome1, position1, strand1, restrictionfragment1,
        // chromosome2, position2, strand2, restrictionfragment2 \n
        // Screen against comments
        if (!line.startsWith("#")) {
            String[] parts = line.split("\t");
            if (parts.length == 9) {
                try {
                    String name = parts[0];
                    int chromosome1 = Integer.parseInt(parts[1]);
                    long position1 = Long.parseLong(parts[2]);
                    int strand1 = Integer.parseInt(parts[3]);
                    int fragment1 = Integer.parseInt(parts[4]);
                    int chromosome2 = Integer.parseInt(parts[5]);
                    long position2 = Long.parseLong(parts[6]);
                    int strand2 = Integer.parseInt(parts[7]);
                    int fragment2 = Integer.parseInt(parts[8]);

                    HiCRead newRead = new HiCRead(name, chromosome1, position1,
                                                  strand1, fragment1,
                                                  chromosome2, position2,
                                                  strand2, fragment2);
                    return newRead;

                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse line " + line
                                       + " expected a number here");
                }

            } else {
                System.err.println("Failed to parse line " + line
                                   + " incorrect column number");
            }
        }
        // Failed to parse this or this was a comment. nothing to return
        return null;
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
        System.out.println("Parser Started");

        // Open file for read
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(sourceFile));
        } catch (FileNotFoundException e1) {
            System.err.println("Failed to find source file: " + sourceFile);
        }

        // init Read Vector
        LinkedBlockingQueue<HiCRead> newReads = new LinkedBlockingQueue<HiCRead>();
        notifyStart(newReads);

        // Read each line
        String line = null;
        try {
            while (reading && reader != null
                   && (line = reader.readLine()) != null) {

                // Parse Line into HiCRead, save it
                HiCRead read = parseLine(line);
                if (read != null) {
                    newReads.put(read);
                    reads.add(read);
                    notifyRead(read);
                }

            }
        } catch (IOException e) {
            System.err.println("Got stuck while trying to read file: @line "
                               + newReads.size());
        } catch (InterruptedException e) {
            System.err.println("Interrupted while putting new read into queue");
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Failed to close reader");
            }
        }
        reading = false;
        notifyComplete();
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
