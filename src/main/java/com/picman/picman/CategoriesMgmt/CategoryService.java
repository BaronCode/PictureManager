package com.picman.picman.CategoriesMgmt;

import java.util.List;

public interface CategoryService {
    Category findById(Integer id);
    Category findByName(String name);
    List<Category> findAll();
    void save(Category category);
    void deleteById(Integer id);
    void deleteByName(String name);
}
