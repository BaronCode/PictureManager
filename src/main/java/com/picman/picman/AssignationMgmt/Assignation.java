package com.picman.picman.AssignationMgmt;

import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.PicturesMgmt.Picture;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "assignations")
public class Assignation {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)                         private 	long 			id;
    @ManyToOne @JoinColumn(name = "picture") @NotNull		                        private     Picture         picture;
    @ManyToOne @JoinColumn(name = "category") @NotNull	                            private     Category        category;

    public Assignation(Picture p, Category c) {
        picture = p;
        category = c;
    }
}
