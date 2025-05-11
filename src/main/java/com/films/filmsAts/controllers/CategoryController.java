package com.films.filmsAts.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.films.filmsAts.dao.UserRepository;
import com.films.filmsAts.dto.CategoryCreateDTO;
import com.films.filmsAts.dto.CategoryResponseDTO;
import com.films.filmsAts.entity.Category;
import com.films.filmsAts.entity.User;
import com.films.filmsAts.security.services.UserDetailsImpl;
import com.films.filmsAts.service.CategoryService;
import jakarta.validation.Valid;
import mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private CategoryService categoryService;
    private UserRepository userRepository;
    @Autowired
    public CategoryController(CategoryService categoryService, UserRepository userRepository)
    {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CategoryResponseDTO> addCategory(@Valid @RequestBody CategoryCreateDTO dto)
    {
        Category category = CategoryMapper.toEntity(dto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        logger.info("Attempting to add category for user ID: {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            logger.warn("User with ID {} not found in the database.", userId);
            return ResponseEntity.badRequest().body(null);
        } else {
            logger.info("Found user: {}", userOptional.get());
            category.setUser(userOptional.get());
        }

        Category categorySaved = categoryService.save(category);

        return ResponseEntity.ok(CategoryMapper.toResponse(categorySaved));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> findAll()
    {

        List<CategoryResponseDTO> categories = categoryService.findAll().stream().map(CategoryMapper::toResponse).collect(Collectors.toList());

        Map<String, Object> response = Map.of("status", "success",
                "results", categories.size(),
                "data", categories);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable Long id)
    {
        Optional<Category> result = Optional.ofNullable(categoryService.findById(Math.toIntExact(id)));

        return result.map(category -> ResponseEntity.ok(CategoryMapper.toResponse(category))).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id)
    {
        Category category = categoryService.findById(Math.toIntExact(id));

        if (category == null)
        {
            return ResponseEntity.notFound().build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if(category.getUser().getId() != userDetails.getId())
        {
            throw new RuntimeException("Can not delete other users category");
        }

        categoryService.deleteById(Math.toIntExact(id));

        return ResponseEntity.ok("Deleted category id - " + id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryCreateDTO dto)
    {
        Category category = categoryService.findById(Math.toIntExact(id));

        if (category == null)
        {
            return ResponseEntity.notFound().build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if(category.getUser().getId() != userDetails.getId())
        {
            throw new RuntimeException("Can not update other users category");
        }

        Category updatedCategory = CategoryMapper.toEntity(dto);
        updatedCategory.setId(id);

        Category saveCategory = categoryService.save(updatedCategory);

        return ResponseEntity.ok(CategoryMapper.toResponse(saveCategory));
    }
}