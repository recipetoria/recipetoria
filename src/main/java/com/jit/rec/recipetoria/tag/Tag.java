package com.jit.rec.recipetoria.tag;

import com.jit.rec.recipetoria.applicationUser.ApplicationUser;
import com.jit.rec.recipetoria.recipe.Recipe;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = "recipes")
@EqualsAndHashCode(exclude = "recipes")
public class Tag {

    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    private Long id;

    @NotNull
    private String name;

    @ManyToOne
    private ApplicationUser applicationUser;

    private String mainPhoto;

    @ManyToMany(mappedBy = "tags")
    private List<Recipe> recipes;
}
