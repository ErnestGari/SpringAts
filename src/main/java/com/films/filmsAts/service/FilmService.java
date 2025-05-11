package com.films.filmsAts.service;

import com.films.filmsAts.dao.FilmRepository;
import com.films.filmsAts.entity.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FilmService {

    List<Film> findAll();
    Film findById(int id);
    Film save(Film film);
    void deleteById(int id);
    List<Film> getFilmsForCurrentUser();
}
