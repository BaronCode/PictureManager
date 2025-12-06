package com.picman.picman.AssignationMgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignationRepo extends JpaRepository<Assignation, Integer> {
}
