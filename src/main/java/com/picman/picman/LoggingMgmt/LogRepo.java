package com.picman.picman.LoggingMgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogRepo extends JpaRepository<Log, Long> {
    @Override
    @Query("select l from Log l order by l.date desc")
    List<Log> findAll();

    @Query("select l from Log l order by l.date desc limit ?1")
    List<Log> findLast(int id);
}
