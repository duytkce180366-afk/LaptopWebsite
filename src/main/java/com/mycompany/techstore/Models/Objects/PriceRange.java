package com.mycompany.techstore.Models.Objects;

public class PriceRange {
    private String label;
    private long min;
    private long max;

    public PriceRange(String label, long min, long max) {
        this.label = label;
        this.min = min;
        this.max = max;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }
}
