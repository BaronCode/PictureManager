package com.picman.picman.PicturesMgmt;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class PictureServiceImplementation implements PictureService {
    private final PictureRepo pictureRepo;

    @Override
    public List<Picture> getLast20Added() {
        return pictureRepo.getLast20Added();
    }

    @Override
    public List<Picture> findAll() {
        return pictureRepo.findAll();
    }

    @Override
    public Picture addPicture(Picture p) {
        return pictureRepo.save(p);
    }

    @Override
    public void deleteById(int id) {
        pictureRepo.deleteById(id);
    }

    @Override
    public Picture getById(int id) {
        return pictureRepo.getReferenceById(id);
    }
}
