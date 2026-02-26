package com.picman.picman.LoggingMgmt;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LogServiceImplementation implements LogService {
    private final LogRepo repo;

    @Override
    public List<Log> findAll() {
        return repo.findAll();
    }

    @Override
    public void save(Log l) {
        repo.save(l);
    }

    @Override
    public List<Log> findLast(int n) {
        if (n == 0) return List.of();
        return repo.findLast(n);
    }
}
