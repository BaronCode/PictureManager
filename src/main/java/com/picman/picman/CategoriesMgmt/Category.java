package com.picman.picman.CategoriesMgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Entity
@Table(name = "categories")
public class Category {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)          				private 	int 			id;
    @Column(nullable = false) @Pattern(regexp = "^(.|\\s)*\\S(.|\\s)*$") @NotNull   private 	String 			name;
                                                                                    private     String          description;

    @Override
    public String toString() {
        return "[" + id + "] " + name + " (" + description + ")";
    }
}
