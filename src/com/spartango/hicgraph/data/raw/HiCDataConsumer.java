package com.spartango.hicgraph.data.raw;

import java.util.concurrent.BlockingQueue;


public interface HiCDataConsumer {

    public void onReadingStarted(BlockingQueue<HiCRead> readQueue);
    public void onReadingComplete();
    public void onNewRead(HiCRead newRead);
}
