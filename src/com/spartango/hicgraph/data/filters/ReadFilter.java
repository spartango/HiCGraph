package com.spartango.hicgraph.data.filters;

import com.spartango.hicgraph.data.raw.HiCRead;

public interface ReadFilter {
    public boolean check(HiCRead r);
}
