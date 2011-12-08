package com.spartango.hicgraph.deploy;

import com.spartango.hicgraph.data.BinaryGraphBuilder;
import com.spartango.hicgraph.data.GraphConsumer;
import com.spartango.hicgraph.data.filters.IntrachromosomalFilter;
import com.spartango.hicgraph.data.raw.HiCParser;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.visualization.GraphImageRenderer;

public class Main {
    
    public static void main(String[] args) {
        // Parse data
        HiCParser parser = new HiCParser("/Volumes/DarkIron/HiC Data/short_data.txt");
        
        // Build Graph
        BinaryGraphBuilder builder = new BinaryGraphBuilder(200, new IntrachromosomalFilter());
        
        // Assemble pipes
        parser.addConsumer(builder);
        builder.addConsumer( new GraphConsumer() {
            
            @Override
            public void onGraphBuilt(ChromatinGraph graph) {
                // Render Graph
                System.out.println("Graph built: "+graph.getEdgeCount()+" edges & "+graph.getVertexCount()+" nodes");
                GraphImageRenderer renderer = new GraphImageRenderer(graph, 8128);
                renderer.saveImage("/Volumes/DarkIron/HiC Data/short_Data.png");
                System.exit(0);
            }
        });
        
        parser.startReading();
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
