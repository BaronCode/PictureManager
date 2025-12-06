package com.picman.picman.SpringAuthentication;

import com.picman.picman.UserMgmt.UserServiceImplementation;
import com.picman.picman.UserMgmt.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private 	final       UserServiceImplementation userService;

    public UserDetailsService(UserServiceImplementation us) {
        userService = us;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userService.findByEmail(username);
        List<Character> userPrivileges = u.getPrivileges().stream().toList();

        Collection<SimpleGrantedAuthority> authorities = userPrivileges.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.toString()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                authorities
        );
    }
}
