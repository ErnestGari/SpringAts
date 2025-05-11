package com.films.filmsAts.controllers;

import com.films.filmsAts.dto.CommentCreateDTO;
import com.films.filmsAts.dto.CommentResponseDTO;
import com.films.filmsAts.entity.Comment;
import com.films.filmsAts.security.services.UserDetailsImpl;
import com.films.filmsAts.service.CommentService;
import com.films.filmsAts.service.FilmService;
import com.films.filmsAts.dao.UserRepository;
import jakarta.validation.Valid;
import mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 4000)
@RestController
@RequestMapping("/api/v1/films/{filmId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final FilmService filmService;
    private final UserRepository userRepository;

    @Autowired
    public CommentController(CommentService commentService, FilmService filmService, UserRepository userRepository) {
        this.commentService = commentService;
        this.filmService = filmService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getCommentsForFilm(@PathVariable Long filmId) {
        List<Comment> comments = commentService.getCommentsByFilmId(filmId);
        return ResponseEntity.ok(comments.stream().map(CommentMapper::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponseDTO> addCommentToFilm(
            @PathVariable Long filmId,
            @Valid @RequestBody CommentCreateDTO commentDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Comment savedComment = commentService.saveComment(commentDTO.getText(), filmId, userId);

        if (savedComment != null) {
            return new ResponseEntity<>(CommentMapper.toResponse(savedComment), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long filmId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentCreateDTO updateDTO) {

        Optional<Comment> commentOptional = commentService.getCommentById(commentId);
        if (commentOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Comment comment = commentOptional.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!comment.getUser().getId().equals(userDetails.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Comment updatedComment = commentService.updateComment(comment, updateDTO.getText());
        return ResponseEntity.ok(CommentMapper.toResponse(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteComment(
            @PathVariable Long filmId,
            @PathVariable Long commentId) {
        Optional<Comment> commentOptional = commentService.getCommentById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (comment.getUser().getId().equals(userDetails.getId())) {
                commentService.deleteComment(commentId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // user only deletes his made comments
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}