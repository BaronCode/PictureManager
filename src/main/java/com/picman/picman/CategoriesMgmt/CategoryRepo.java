package com.picman.picman.CategoriesMgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {

    @Query("select c from Category c where c.name = ?1")
    Category findByName(String name);

    @Modifying
    @Query("delete from Category c where c.name = ?1")
    void deleteByName(String name);
}
