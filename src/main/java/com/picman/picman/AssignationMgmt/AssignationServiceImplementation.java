package com.picman.picman.AssignationMgmt;

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
    public List<Picture> getAssignationsByCategoryList(int[] categories) {
        return assignationRepo.getAssignationsByCategoryList(categories);
    }
}
