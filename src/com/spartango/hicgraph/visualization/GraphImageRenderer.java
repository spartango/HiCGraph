package com.spartango.hicgraph.visualization;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.functors.ConstantTransformer;

import com.spartango.hicgraph.model.ChromatinGraph;
import com.spartango.hicgraph.model.ChromatinLocation;
import com.spartango.hicgraph.model.ChromatinRelation;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

public class GraphImageRenderer {
    private Layout<ChromatinLocation, ChromatinRelation>                   layout;
    private VisualizationImageServer<ChromatinLocation, ChromatinRelation> server;
    private int                                                            imageSize;

    public GraphImageRenderer(ChromatinGraph graph, int size) {
        imageSize = size;
        layout = new FRLayout<ChromatinLocation, ChromatinRelation>(graph);
        server = new VisualizationImageServer<ChromatinLocation, ChromatinRelation>(
                                                                                    layout,
                                                                                    new Dimension(
                                                                                                  size,
                                                                                                  size));
        server.getRenderContext()
              .setVertexShapeTransformer(new ConstantTransformer(
                                                                 new Ellipse2D.Float(
                                                                                     -4,
                                                                                     -4,
                                                                                     8,
                                                                                     8)));
    }

    public void setGraph(ChromatinGraph g) {
        layout = new FRLayout<ChromatinLocation, ChromatinRelation>(g);
        server.setGraphLayout(layout);
    }

    public Image getImage() {
        return server.getImage(new Point2D.Double(imageSize / 2, imageSize / 2),
                               new Dimension(imageSize, imageSize));
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
