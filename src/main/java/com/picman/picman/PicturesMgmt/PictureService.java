package com.picman.picman.PicturesMgmt;

import java.util.List;

public interface PictureService {
    List<Picture> getLast20Added();
    List<Picture> findAll();
    Picture addPicture(Picture p);
    void deleteById(int id);
    Picture getById(int id);
}
