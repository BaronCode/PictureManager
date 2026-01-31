package com.picman.picman.CategoriesMgmt;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Repository @Table
public interface CategoryRepo extends JpaRepository<Category, Integer> {

    @Query("select c from Category c where c.name = ?1")
    Category findByName(String name);

    @Override
    @Query("select c from Category c order by c.id asc")
    List<Category> findAll();

    @Modifying
    @Query("delete from Category c where c.name = ?1")
    void deleteByName(String name);

    @Modifying
    @Transactional
    @Query("update Category c set c.name = ?2, c.description = ?3 where c.id =?1")
    void updateCategory(int id, String name, String description);
}
