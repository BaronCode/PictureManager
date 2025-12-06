package com.picman.picman.UserMgmt;

import java.util.List;

public interface UserService {
    User findById(Long id);
    User findByName(String name);
    User findByEmail(String email);
    List<User> findAll();
    void save(User category);
    void deleteById(Long id);
    void deleteByName(String name);
    void editPrivileges(long current, long subject, char privilege, boolean value);
}
