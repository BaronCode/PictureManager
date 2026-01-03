package com.picman.picman.PicturesMgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.relational.core.mapping.Table;
import java.util.List;

@Repository @Table
public interface PictureRepo extends JpaRepository<Picture, Integer> {
    @Query("select p from Picture p order by p.dateadded limit 20")
    List<Picture> getLast20Added();
}
