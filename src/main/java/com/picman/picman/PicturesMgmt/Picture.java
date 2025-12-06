package com.picman.picman.PicturesMgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Entity
@Table(name = "pictures")
public class Picture {
    @Id @NotNull @GeneratedValue(strategy = GenerationType.IDENTITY)                private     long                id;
    @NotNull @Column(nullable = false, length = 255, unique = true)                                                                       private     String              path;
    @NotNull @Column(nullable = false)                                                                       private     LocalDateTime       dateAdded;
    @NotNull @Column(nullable = false)                                                                       private     boolean             protection;

    @PrePersist
    public void prePersist() {
        if (dateAdded == null) {
            dateAdded = LocalDateTime.now();
        }
    }
}
