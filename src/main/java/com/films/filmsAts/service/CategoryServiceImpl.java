package com.films.filmsAts.service;

import com.films.filmsAts.dao.CategoryRepository;
import com.films.filmsAts.entity.Category;
import com.films.filmsAts.entity.Film;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(int id) {
        Optional<Category> result = categoryRepository.findById((long) id);

        Category category = null;

        if(result.isPresent()){
            category = result.get();
        }
        else{
            throw new RuntimeException("Did not find category by id - " + id);
        }
        return category;
    }

    @Override
    @Transactional
    public Category save(Category category) {
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());

        if (existingCategory.isPresent())
        {
            throw new RuntimeException("Category already exists - " + category.getName());
        }

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        categoryRepository.deleteById((long) id);
    }
}
