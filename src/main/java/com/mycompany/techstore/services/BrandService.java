package com.mycompany.techstore.Services;

import com.mycompany.techstore.Models.Objects.Brand;
import com.mycompany.techstore.Repositories.BrandRepository;
import java.util.List;

public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService() {
        brandRepository = new BrandRepository();
    }

    public List<Brand> getAllBrands() {
        return brandRepository.getAllBrands();
    }

    public Brand getBrandById(int id) {
        return brandRepository.getBrandById(id);
    }

    public void addBrand(Brand brand) {
        brandRepository.insert(brand);
    }

    public void updateBrand(Brand brand) {
        brandRepository.update(brand);
    }

    public void deleteBrand(int id) {
        brandRepository.delete(id);
    }
}