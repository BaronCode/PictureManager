package com.picman.picman.PicturesMgmt;

import com.drew.lang.Rational;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository @Table
public interface PictureRepo extends JpaRepository<Picture, Integer> {
    @Query("select p from Picture p order by p.dateadded desc limit 20")
    List<Picture> getLast20Added();

    @Transactional
    @Modifying
    @Query("update Picture set sizekb=?2, shotat=?3, height=?4, width=?5, aperture=?6, iso=?7, focallength=?8, exposurenum=?9, exposureden=?10, cameramodel=?11 where id=?1")
    void updateMetadata(long pid, BigDecimal skb, LocalDateTime shot, Integer h, Integer w, BigDecimal ap, Integer iso, Short foc, Integer exnum, Integer exden, String cam);

}
