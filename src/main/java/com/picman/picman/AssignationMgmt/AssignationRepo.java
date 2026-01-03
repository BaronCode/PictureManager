package com.picman.picman.AssignationMgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.relational.core.mapping.Table;

@Repository @Table
public interface AssignationRepo extends JpaRepository<Assignation, Integer> {
}
