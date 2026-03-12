package com.picman.picman.UserMgmt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)                         private     int             id;
    @Column(nullable = false, length = 30)                                          private     String          name;
    @Column(nullable = false, unique = true)                                        private     String          email;
    @Column(nullable = false)                                                       private     String          password;
    @Column(nullable = false, length = 3) @Convert(converter = PrvConverter.class)  private     Set<Character>  privileges;
    @Column(nullable = false, length = 128)                                         private     String          organization;

    /*  PRIVILEGES
        [o] - owner
        [u] - user control
        [w] - write
        [d] - delete
        [r] - readonly
        [s] - support
    */

    public User(String name, String email, String password, Set<Character> privileges, String organization) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.privileges = privileges;
        this.organization = organization;
    }

    @PrePersist
    public void prePersist() {
        if (name == null || name.isEmpty() || (name.equals("admin") && !email.equals("admin@picman.it")))  {
            name = "user";
        }
        if (privileges == null) {
            privileges = new HashSet<>();
        }
    }

    public void editPrivileges(char privilege, boolean value) {
        Set<Character> newPrivileges = new HashSet<>(privileges);
        if (value) {
            newPrivileges.add(privilege);
        } else newPrivileges.remove(privilege);
        setPrivileges(newPrivileges);
    }

    public boolean hasAuthorities(char... authorities) {
        List<Character> auth = new ArrayList<>();
        for (char c : authorities) auth.add(c);
        return privileges.containsAll(auth);
    }

    public boolean hasAuthority(char authority) {
        return privileges.contains(authority);
    }

    public boolean hasAnyAuthority(char... authority) {
        return Collections.disjoint(privileges, Set.of(authority));
    }

    @Override
    public String toString() {
        return getName() + "[" + getId() + ":" + getEmail() + "]";
    }

    public String logToString() { return getName() + " [" + getId() + "]"; }
}
