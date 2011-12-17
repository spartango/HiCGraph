package com.spartango.hicgraph.model;

public class ChromatinRelation {
    private ChromatinLocation firstLoc;
    private ChromatinLocation secondLoc;

    private double            coexpressionCorrelation;
    private double            certainty;

    public ChromatinRelation(ChromatinLocation firstLoc,
                             ChromatinLocation secondLoc) {
        this.firstLoc = firstLoc;
        this.secondLoc = secondLoc;
        coexpressionCorrelation = 0;
        certainty = 1;
    }

    public double getCertainty() {
        return certainty;
    }

    public void setCertainty(double certainty) {
        this.certainty = certainty;
    }

    public double getCoexpressionCorrelation() {
        return coexpressionCorrelation;
    }

    public void setCoexpressionCorrelation(double coexpressionCorrelation) {
        this.coexpressionCorrelation = coexpressionCorrelation;
    }

    public ChromatinLocation getFirstLoc() {
        return firstLoc;
    }

    public ChromatinLocation getSecondLoc() {
        return secondLoc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        if (obj instanceof ChromatinRelation) {
            ChromatinRelation target = (ChromatinRelation) obj;
            return ((target.firstLoc.equals(firstLoc) || target.firstLoc.equals(secondLoc))
                    && (target.secondLoc.equals(firstLoc) || target.secondLoc.equals(firstLoc)) && !(target.firstLoc.equals(target.secondLoc)));

        } else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + firstLoc.hashCode();
        hash = hash * 31 + secondLoc.hashCode();
        return hash;
    }

}
