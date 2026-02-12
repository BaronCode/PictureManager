package com.picman.picman.PicturesMgmt;

import com.drew.lang.Rational;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PictureService {
    List<Picture> getLastAdded(int max);
    List<Picture> findAll();
    Picture addPicture(Picture p);
    void deleteById(int id);
    Picture getById(int id);
    void updateMetadata(long pid, BigDecimal skb, LocalDateTime shot, Integer h, Integer w, BigDecimal ap, Integer iso, Short foc, Integer exnum, Integer exden, String cam);
}
