package com.spartango.hicgraph.analysis.stat;

import java.util.concurrent.BlockingQueue;

import com.spartango.hicgraph.analysis.cluster.ClusterConsumer;
import com.spartango.hicgraph.model.ChromatinGraph;

public class StatisticGatherer implements ClusterConsumer {

    @Override
    public void onClusteringStarted(ChromatinGraph source,
                                    BlockingQueue<ChromatinGraph> clusterQueue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClusterFound(ChromatinGraph cluster) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClusteringComplete() {

    }

}
