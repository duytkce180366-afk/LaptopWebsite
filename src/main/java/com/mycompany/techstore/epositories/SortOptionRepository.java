package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.SortOption;
import java.util.*;

public class SortOptionRepository {
    private static final List<SortOption> sortOptions = new ArrayList<>();
    
    static {
        initializeSortOptions();
    }
    
    private static void initializeSortOptions() {
        sortOptions.add(new SortOption("Recommended", "recommended"));
        sortOptions.add(new SortOption("Price low to high", "price-asc"));
        sortOptions.add(new SortOption("Price high to low", "price-desc"));
    }
    
    public static List<SortOption> getAll() {
        return new ArrayList<>(sortOptions);
    }
}
