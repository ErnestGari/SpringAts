package mapper;

import com.films.filmsAts.dto.CategoryCreateDTO;
import com.films.filmsAts.dto.CategoryResponseDTO;
import com.films.filmsAts.entity.Category;

public class CategoryMapper {

    public static Category toEntity(CategoryCreateDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public static CategoryResponseDTO toResponse(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getUser() != null) {
            dto.setUserId(category.getUser().getId());
        } else {
            dto.setUserId(null);
        }
        return dto;
    }
}