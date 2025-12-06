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
    public void save(User User) {
        userRepo.save(User);
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
    public void editPrivileges(long current, long subject ,char privilege, boolean value) {
        if (userRepo.getReferenceById(current).getPrivileges().contains('a')) {
            userRepo.getReferenceById(subject).editPrivileges(privilege, value);
        }
    }
}
