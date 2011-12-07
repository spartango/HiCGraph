package com.spartango.hicgraph.data;

import java.util.List;


public interface HiCDataSource {
    public void addConsumer(HiCDataConsumer l);
    
    public void removeConsumer(HiCDataConsumer l);
    
    public void startReading();

    public void stopReading();
    
    public List<HiCRead> readAll();
    
}

