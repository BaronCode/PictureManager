package com.picman.picman.AssignationMgmt;

import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.PicturesMgmt.Picture;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignationServiceImplementation implements AssignationService {
    private final AssignationRepo assignationRepo;

    public AssignationServiceImplementation(AssignationRepo ar) {
        assignationRepo = ar;
    }

    @Override
    public List<Assignation> findAll() {
        return assignationRepo.findAll();
    };

    @Override
    public List<PicturesCategories> getAssignationsGroup() {
        return assignationRepo.getAssignationsGroup();
    }

    @Override
    public void addAssignation(Assignation a) {
        assignationRepo.save(a);
    }

    @Override
    public void removeAssignation(Assignation a) {
        assignationRepo.deleteByPair(a.getPicture(), a.getCategory());
    }

    @Override
    public List<Picture> getAssignationsByCategoryList(int[] categories) {
        return assignationRepo.getAssignationsByCategoryList(categories);
    }

    @Override
    public List<Category> getCategoriesByPictureId(long id) {
        return assignationRepo.getCategoriesByPictureId(id);
    }

}
