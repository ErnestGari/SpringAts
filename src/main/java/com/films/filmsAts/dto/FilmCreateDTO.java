package com.films.filmsAts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FilmCreateDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @DecimalMin(value = "1.0", message = "IMDB ranking must be above a 1")
    @DecimalMax(value = "10.0", message = "IMDB ranking must be below a 10")
    private Double imdb;

    @NotNull(message = "A film must be associated with a category")
    @JsonProperty("category_id")
    private Long categoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getImdb() {
        return imdb;
    }

    public void setImdb(Double imdb) {
        this.imdb = imdb;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "FilmCreateDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imdb=" + imdb +
                ", categoryId=" + categoryId +
                '}';
    }
}