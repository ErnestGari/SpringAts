package mapper;

import com.films.filmsAts.dto.FilmCreateDTO;
import com.films.filmsAts.dto.FilmResponseDTO;
import com.films.filmsAts.entity.Category;
import com.films.filmsAts.entity.Film;

public class FilmMapper {

    public static Film toEntity(FilmCreateDTO dto) {
        Film film = new Film();
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setImdb(dto.getImdb());

        Category category = new Category();
        category.setId(dto.getCategoryId());
        film.setCategory(category);

        return film;
    }

    public static FilmResponseDTO toResponse(Film entity) {
        FilmResponseDTO dto = new FilmResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImdb(entity.getImdb());
        dto.setUserId(entity.getUser().getId());
        return dto;
    }
}