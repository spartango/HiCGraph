package com.spartango.hicgraph.data;

import java.util.Vector;

import com.spartango.hicgraph.model.ChromatinGraph;

public interface GraphBuilder {

	public ChromatinGraph buildChromatinGraph(Vector<HiCRead> reads);
}
