package com.picman.picman.AssignationMgmt;

import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.PicturesMgmt.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Repository @Table
public interface AssignationRepo extends JpaRepository<Assignation, Integer> {

    /*Not using SELECT a FROM Assignation a, because it's a native query*/
    @Query(value = "SELECT * FROM pictures p WHERE p.id = ANY (SELECT a.picture AS picId FROM assignations a WHERE a.category = ANY(?1) GROUP BY a.picture HAVING COUNT(DISTINCT a.category) = CARDINALITY(?1)) ", nativeQuery = true)
    List<Picture> getAssignationsByCategoryList(int[] categories);

    @Query("SELECT c FROM Category c INNER JOIN (SELECT a.category AS cat FROM Assignation a WHERE a.picture.id = ?1) subq ON c.id = subq.cat.id")
    List<Category> getCategoriesByPictureId(long id);
}
