package com.picman.picman.PicturesMgmt;

import com.picman.picman.Exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service @AllArgsConstructor
public class PictureServiceImplementation implements PictureService {
    private final PictureRepo pictureRepo;

    @Override
    public List<Picture> getLastAdded(int max) {
        return pictureRepo.getLastAdded(max);
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
        return pictureRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(PictureRepo.class, "Unable to find Picture with id " + id));
    }

    @Override
    public void updateMetadata(long pid, BigDecimal skb, LocalDateTime shot, Integer h, Integer w, BigDecimal ap, Integer iso, Short foc, Integer exnum, Integer exden, String cam) {
        pictureRepo.updateMetadata(pid, skb, shot, h, w, ap, iso, foc, exnum, exden, cam);
    }

    @Override
    public boolean existsByPath(String path) {
        return pictureRepo.existsByPath(path);
    }
}
