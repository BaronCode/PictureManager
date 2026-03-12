package com.picman.picman.UserMgmt;

import java.util.List;

public interface UserService {
    User findById(Long id);
    User findByName(String name);
    User findByEmail(String email);
    List<User> findAll();
    User save(User category);
    void deleteById(Long id);
    void deleteByName(String name);
    void editPrivileges(User u, char privilege, boolean value);
}
