package com.films.filmsAts.dao;

import com.films.filmsAts.entity.Category;
import com.films.filmsAts.entity.Role;
import com.films.filmsAts.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
