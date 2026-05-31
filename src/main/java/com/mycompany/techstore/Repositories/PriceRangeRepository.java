package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.PriceRange;
import java.util.*;

public class PriceRangeRepository {
    private static final List<PriceRange> priceRanges = new ArrayList<>();
    
    static {
        initializePriceRanges();
    }
    
    private static void initializePriceRanges() {
        priceRanges.add(new PriceRange("All prices", 0, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Under 300K", 0, 300000));
        priceRanges.add(new PriceRange("300K - 700K", 300000, 700000));
        priceRanges.add(new PriceRange("700K - 1.2M", 700000, 1200000));
        priceRanges.add(new PriceRange("Under 500K", 0, 500000));
        priceRanges.add(new PriceRange("500K - 1M", 500000, 1000000));
        priceRanges.add(new PriceRange("Under 1M", 0, 1000000));
        priceRanges.add(new PriceRange("1M - 2M", 1000000, 2000000));
        priceRanges.add(new PriceRange("1M - 5M", 1000000, 5000000));
        priceRanges.add(new PriceRange("2M - 3M", 2000000, 3000000));
        priceRanges.add(new PriceRange("2M - 4M", 2000000, 4000000));
        priceRanges.add(new PriceRange("3M - 5M", 3000000, 5000000));
        priceRanges.add(new PriceRange("4M - 7M", 4000000, 7000000));
        priceRanges.add(new PriceRange("4M - 8M", 4000000, 8000000));
        priceRanges.add(new PriceRange("5M - 8M", 5000000, 8000000));
        priceRanges.add(new PriceRange("5M - 15M", 5000000, 15000000));
        priceRanges.add(new PriceRange("7M - 12M", 7000000, 12000000));
        priceRanges.add(new PriceRange("8M - 12M", 8000000, 12000000));
        priceRanges.add(new PriceRange("8M - 15M", 8000000, 15000000));
        priceRanges.add(new PriceRange("10M - 15M", 10000000, 15000000));
        priceRanges.add(new PriceRange("15M - 20M", 15000000, 20000000));
        priceRanges.add(new PriceRange("15M - 25M", 15000000, 25000000));
        priceRanges.add(new PriceRange("15M - 30M", 15000000, 30000000));
        priceRanges.add(new PriceRange("20M - 30M", 20000000, 30000000));
        priceRanges.add(new PriceRange("20M - 25M", 20000000, 25000000));
        priceRanges.add(new PriceRange("25M - 30M", 25000000, 30000000));
        priceRanges.add(new PriceRange("30M - 40M", 30000000, 40000000));
        priceRanges.add(new PriceRange("Under 3M", 0, 3000000));
        priceRanges.add(new PriceRange("Under 4M", 0, 4000000));
        priceRanges.add(new PriceRange("Under 8M", 0, 8000000));
        priceRanges.add(new PriceRange("Under 10M", 0, 10000000));
        priceRanges.add(new PriceRange("Under 20M", 0, 20000000));
        priceRanges.add(new PriceRange("Over 1.2M", 1200000, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Over 2M", 2000000, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Over 3M", 3000000, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Over 4M", 4000000, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Over 8M", 8000000, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Over 12M", 12000000, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Over 25M", 25000000, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Over 30M", 30000000, Long.MAX_VALUE));
        priceRanges.add(new PriceRange("Over 40M", 40000000, Long.MAX_VALUE));
    }
    
    public static List<PriceRange> getAll() {
        return new ArrayList<>(priceRanges);
    }
}
