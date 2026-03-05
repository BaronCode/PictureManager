package com.picman.picman.PicturesMgmt;

import com.drew.lang.Rational;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PictureService {
    List<Picture> getLastAdded(int max);
    List<Picture> findAll();
    Picture save(Picture p);
    void deleteById(int id);
    Picture getById(int id);
    boolean existsByPath(String path);
    void updateMetadata(long pid, BigDecimal skb, LocalDateTime shot, Integer h, Integer w, BigDecimal ap, Integer iso, Short foc, Integer exnum, Integer exden, String cam);

    List<Picture> getAllOrdered();
    List<Picture> findAllSorted(Sort sort);
}
