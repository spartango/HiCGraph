package com.spartango.hicgraph.analysis;

import java.util.Set;

import com.spartango.hicgraph.model.ChromatinLocation;

public interface ClusterConsumer {
    
    public void onClusteringComplete(Set<Set<ChromatinLocation>> clusters); 
    
}
