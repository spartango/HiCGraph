package com.spartango.hicgraph.deploy;

import com.spartango.hicgraph.data.BinaryGraphBuilder;
import com.spartango.hicgraph.data.GraphConsumer;
import com.spartango.hicgraph.data.HiCParser;
import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.visualization.GraphImageRenderer;

public class Main {
    
    public static void main(String[] args) {
        // Parse data
        HiCParser parser = new HiCParser("GSM455133_30E0LAAXX.1.maq.hic.summary.binned.txt");
        
        // Build Graph
        BinaryGraphBuilder builder = new BinaryGraphBuilder(50);
        
        // Assemble pipes
        parser.addConsumer(builder);
        builder.addConsumer( new GraphConsumer() {
            
            @Override
            public void onGraphBuilt(ChromatinGraph graph) {
                // Render Graph
                GraphImageRenderer renderer = new GraphImageRenderer(graph);
                renderer.saveImage("out.png");
                System.exit(0);
            }
        });
        
        parser.startReading();
        while(true) {
            Thread.yield();
        }
    }
}
