package com.picman.picman.UserMgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class User {
    @Id @NotNull @GeneratedValue(strategy = GenerationType.IDENTITY)                private     int                id;
    @NotNull @Column(nullable = false, length = 30)                                                                        private     String              name;
    @NotNull @Column(nullable = false, length = 255, unique = true)                                                                        private     String              email;
    @NotNull @Column(nullable = false, length = 255)                                                          private     String              password;
    @NotNull @Column(nullable = false, length = 3) @Convert(converter = PrivilegesConverter.class)                                                                       private Set<Character> privileges;
    @NotNull @Column(nullable = false, length = 128)                                                    private String organization;
    /*  PRIVILEGES
        [o] - owner
        [u] - user control
        [w] - write
        [d] - delete
        [r] - readonly
        [s] - support
    */

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
        if (value) {
            privileges.add(privilege);
        } else privileges.remove(privilege);
    }

    @Override
    public String toString() {
        return getName() + "[" + getId() + ":" + getEmail() + "]";
    }
}
