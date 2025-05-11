package com.films.filmsAts.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.films.filmsAts.dao.UserRepository;
import com.films.filmsAts.dto.FilmCreateDTO;
import com.films.filmsAts.dto.FilmResponseDTO;
import com.films.filmsAts.entity.Category;
import com.films.filmsAts.entity.Film;
import com.films.filmsAts.entity.User;
import com.films.filmsAts.security.services.UserDetailsImpl;
import com.films.filmsAts.service.FilmService;
import jakarta.validation.Valid;
import mapper.FilmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 4000)
@RestController
@RequestMapping("/api/v1/films")
public class FilmController {

    private final FilmService filmService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Autowired
    public FilmController(FilmService theFilmService, ObjectMapper theObjectMapper, UserRepository userRepository) {
        this.filmService = theFilmService;
        this.objectMapper = theObjectMapper;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        List<FilmResponseDTO> films = filmService.getFilmsForCurrentUser().stream().map(FilmMapper::toResponse).collect(Collectors.toList());
        Map<String, Object> response = Map.of("status", "success", "results", films.size(), "data", films);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<FilmResponseDTO> getFilm(@PathVariable Long filmId) {
        Optional<Film> results = Optional.ofNullable(filmService.findById(filmId.intValue()));
        return results.map(film -> ResponseEntity.ok(FilmMapper.toResponse(film))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FilmResponseDTO> addFilm(@Valid @RequestBody FilmCreateDTO filmDTO) {

        Film mapped = FilmMapper.toEntity(filmDTO);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("Authenticated User ID: " + userDetails.getId());

        Optional<User> userOptional = userRepository.findById(userDetails.getId());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        mapped.setUser(userOptional.get());

        Film saved = filmService.save(mapped);

        return ResponseEntity.ok(FilmMapper.toResponse(saved));
    }

    @DeleteMapping("/{filmId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFilm(@PathVariable int filmId) {
        Film theFilm = filmService.findById(filmId);
        if (theFilm == null) {
            return ResponseEntity.notFound().build();
        }
        filmService.deleteById(filmId);
        return ResponseEntity.ok("Deleted film id - " + filmId);
    }

    @PutMapping("/{filmId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FilmResponseDTO> updateFilm(@PathVariable int filmId, @RequestBody FilmCreateDTO film) {
        Film existing = filmService.findById(filmId);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Film updatedFilm = FilmMapper.toEntity(film);
        updatedFilm.setId((long) filmId);
        updatedFilm.setUser(existing.getUser());
        Film saved = filmService.save(updatedFilm);
        return ResponseEntity.ok(FilmMapper.toResponse(saved));
    }

    @PatchMapping("/{filmId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FilmResponseDTO> pathFilm(@PathVariable Integer filmId, @RequestBody Map<String, Object> pathPayLoad) {
        Film tempfilm = filmService.findById(filmId.intValue());
        if (tempfilm == null) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!tempfilm.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.notFound().build();
        }
        if (pathPayLoad.containsKey("id")) {
            return ResponseEntity.badRequest().body(null);
        }
        ObjectNode filmNode = objectMapper.convertValue(tempfilm, ObjectNode.class);
        ObjectNode pathNode = objectMapper.convertValue(pathPayLoad, ObjectNode.class);
        filmNode.setAll(pathNode);
        Film patchedFilm = objectMapper.convertValue(filmNode, Film.class);
        Film saved = filmService.save(patchedFilm);
        return ResponseEntity.ok(FilmMapper.toResponse(saved));
    }

    private Film apply(Map<String, Object> pathPayload, Film film) {
        ObjectNode filmNode = objectMapper.convertValue(film, ObjectNode.class);
        ObjectNode pathNode = objectMapper.convertValue(pathPayload, ObjectNode.class);
        filmNode.setAll(pathNode);
        return objectMapper.convertValue(filmNode, Film.class);
    }
}