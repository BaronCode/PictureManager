package com.picman.picman.LoggingMgmt;

import java.util.List;

public interface LogService {
    List<Log> findAll();
    void save(Log l);
    List<Log> findLast(int n);
}
