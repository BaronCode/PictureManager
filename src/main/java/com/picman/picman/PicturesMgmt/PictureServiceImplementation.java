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
}
