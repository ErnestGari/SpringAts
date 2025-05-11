package com.films.filmsAts.dto;

public class CategoryResponseDTO {

    private Long id;
    private String name;
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name; // Renamed getter
    }

    public void setName(String name) { // Renamed setter
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}