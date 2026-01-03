package com.picman.picman.AssignationMgmt;

import com.picman.picman.PicturesMgmt.Picture;

import java.util.List;

public interface AssignationService {
    List<Picture> getAssignationsByCategoryList(int[] categories);
}
