package com.picman.picman.AssignationMgmt;

import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.PicturesMgmt.Picture;

import java.util.List;

public interface AssignationService {
    List<Assignation> findAll();

    List<PicturesCategories> getAssignationsGroup();

    List<Picture> getAssignationsByCategoryList(int[] categories);

    List<Category> getCategoriesByPictureId(long id);
}
