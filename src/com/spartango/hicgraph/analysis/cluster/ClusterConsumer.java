package com.spartango.hicgraph.analysis.cluster;

import java.util.concurrent.BlockingQueue;

import com.spartango.hicgraph.model.ChromatinGraph;

public interface ClusterConsumer {

    public void onClusteringStarted(ChromatinGraph source,
                                    BlockingQueue<ChromatinGraph> clusterQueue);

    public void onClusterFound(ChromatinGraph cluster);

    public void onClusteringComplete();

}
