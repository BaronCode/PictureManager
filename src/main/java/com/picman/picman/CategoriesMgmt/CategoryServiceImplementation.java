package com.picman.picman.CategoriesMgmt;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class CategoryServiceImplementation implements CategoryService {
    private         final       CategoryRepo        categoryRepo;

    @Override
    public Category findById(Integer id) {
        return categoryRepo.findById(id).orElse(null);
    }

    public Category findByName(String name) {
        return categoryRepo.findByName(name);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepo.findAll();
    }

    @Override
    public void save(Category category) {
        categoryRepo.save(category);
    }

    @Override
    public void deleteById(Integer id) {
        categoryRepo.deleteById(id);
    }

    @Override
    public void deleteByName(String name) {
        categoryRepo.deleteByName(name);
    }
}
