package com.films.filmsAts.service;

import com.films.filmsAts.dao.FilmRepository;
import com.films.filmsAts.entity.Film;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmServiceImpl implements FilmService {

    private FilmRepository filmRepository;

    @Autowired
    public FilmServiceImpl(FilmRepository filmRepository){
        this.filmRepository = filmRepository;
    }

    @Override
    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    @Override
    public Film findById(int id) {
        Optional<Film> result = filmRepository.findById((long) id);

        Film film = null;

        if(result.isPresent()){
            film = result.get();
        }
        else{
            throw new RuntimeException("Did not find film by id - " + id);
        }
        return film;
    }

    @Override
    @Transactional
    public Film save(Film film) {
        return filmRepository.save(film);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        filmRepository.deleteById((long) id);
    }

    @Override
    public List<Film> getFilmsForCurrentUser() {
        return List.of();
    }
}
