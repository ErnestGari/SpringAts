package com.films.filmsAts.service;

import com.films.filmsAts.entity.Comment;
import com.films.filmsAts.dao.CommentRepository;
import com.films.filmsAts.dao.FilmRepository;
import com.films.filmsAts.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService{

    private final CommentRepository commentRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, FilmRepository filmRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    public List<Comment> getCommentsByFilmId(Long filmId) {
        return commentRepository.findByFilmId(filmId);
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Transactional
    public Comment saveComment(String text, Long filmId, Long userId) {
        return filmRepository.findById(filmId)
                .flatMap(film -> userRepository.findById(userId)
                        .map(user -> {
                            Comment comment = new Comment(text, film, user);
                            return commentRepository.save(comment);
                        }))
                .orElse(null);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public Comment updateComment(Comment comment, String newText) {
        comment.setText(newText);
        return commentRepository.save(comment);
    }
}