package com.films.filmsAts.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryCreateDTO {
    @NotBlank(message = "Category name can not be blank")
    private String name;

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Category name can not be blank") String name) {
        this.name = name;
    }
}