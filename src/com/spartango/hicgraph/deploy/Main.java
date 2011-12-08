package com.spartango.hicgraph.deploy;

import com.spartango.hicgraph.data.BinaryGraphBuilder;
import com.spartango.hicgraph.data.GraphConsumer;
import com.spartango.hicgraph.data.filters.IntrachromosomalFilter;
import com.spartango.hicgraph.data.raw.*;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.visualization.GraphImageRenderer;

public class Main {
    
    public static void main(String[] args) {
        // Parse data
        //HiCDataSource dataSource = new HiCParser("/Volumes/DarkIron/HiC Data/short_data.txt");
        HiCDataSource dataSource = new ControlDataSource(1350, 1); 
        // Build Graph
        BinaryGraphBuilder builder = new BinaryGraphBuilder(50000, new IntrachromosomalFilter(1));
        
        // Assemble pipes
        dataSource.addConsumer(builder);
        builder.addConsumer( new GraphConsumer() {
            
            @Override
            public void onGraphBuilt(ChromatinGraph graph) {
                // Render Graph
                System.out.println("Graph built: "+graph.getEdgeCount()+" edges & "+graph.getVertexCount()+" nodes");
                GraphImageRenderer renderer = new GraphImageRenderer(graph, 8128);
                renderer.saveImage("/Volumes/DarkIron/HiC Data/test_single_control.png");
                System.exit(0);
            }
        });
        
        dataSource.startReading();
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
