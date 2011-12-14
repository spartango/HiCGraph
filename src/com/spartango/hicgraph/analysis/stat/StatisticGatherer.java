package com.spartango.hicgraph.analysis.stat;

import java.util.Set;

import org.apache.commons.collections15.Predicate;

import com.spartango.hicgraph.analysis.cluster.ClusterConsumer;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;

public class StatisticGatherer implements ClusterConsumer {

    @Override
    public void onClusteringComplete(Set<Set<ChromatinLocation>> clusters,
                                     ChromatinGraph graph) {
        System.out.println(clusters.size() + " Clusters found");
        
        double clusterSize; 
        double clusterSizeDev;
        
        
        for (Set<ChromatinLocation> cluster : clusters) {
            if (cluster.size() > 4) {
                double expressionSum = 0;
                
                
                
                ChromatinGraph induced = FilterUtils.createInducedSubgraph(cluster,
                                                                                 graph);
                
                for (ChromatinRelation rel : induced.getEdges()) {
                    expressionSum += rel.getCoexpressionCorrelation();
                }

                // expressionSum /= cluster.size();
                System.out.println("Cluster: " + induced.getVertexCount()
                                   + " nodes " + induced.getEdgeCount()
                                   + " edges -> " + expressionSum + " coex");
            }
        }

        System.exit(0);

    }

}
