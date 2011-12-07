package com.spartango.hicgraph.visualization;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

public class GraphImageRenderer {
    private FRLayout<ChromatinLocation, ChromatinRelation>                 layout;
    private VisualizationImageServer<ChromatinLocation, ChromatinRelation> server;

    public GraphImageRenderer(ChromatinGraph graph) {
        layout = new FRLayout<ChromatinLocation, ChromatinRelation>(graph);
        server = new VisualizationImageServer<ChromatinLocation, ChromatinRelation>(
                                                                                    layout,
                                                                                    new Dimension(
                                                                                                  1600,
                                                                                                  1200));
    }

    public Image getImage() {
        return server.getImage(new Point2D.Double(800, 600),
                               new Dimension(1600, 1200));
    }

    public void saveImage(String filename) {
        File f = new File(filename);
        try {
            ImageIO.write((BufferedImage) getImage(), "png", f);
            System.out.println("Rendered Image");
        } catch (IOException e) {
            System.err.println("Failed to save image file");
        }

    }
}
