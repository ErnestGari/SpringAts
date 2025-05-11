package com.films.filmsAts.dao;

import com.films.filmsAts.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByFilmId(Long filmId);

    List<Comment> findByUserId(Long userId);

    List<Comment> findByFilmIdAndUserId(Long filmId, Long userId);
}