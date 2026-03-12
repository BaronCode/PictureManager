package com.picman.picman.UserMgmt;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class UserServiceImplementation implements UserService {
    private         final       UserRepo        userRepo;

    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public User findByName(String name) {
        return userRepo.findByName(name);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public User save(User User) {
        return userRepo.save(User);
    }

    @Override
    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public void deleteByName(String name) {
        userRepo.deleteByName(name);
    }

    @Override
    public void editPrivileges(User u, char privilege, boolean value) {
        u.editPrivileges(privilege, value);
        save(u);
    }
}
