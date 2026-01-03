package com.picman.picman.UserMgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.relational.core.mapping.Table;

@Repository @Table
public interface UserRepo extends JpaRepository<User, Long> {

    @Query("select u from User u where u.name = ?1")
    User findByName(String name);

    @Modifying
    @Query("delete from User u where u.name = ?1")
    void deleteByName(String name);

    @Query("select u from User u where u.email = ?1")
    User findByEmail(String email);
}
