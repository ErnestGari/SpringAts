package com.films.filmsAts.dao;

import com.films.filmsAts.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Long> {
}
