package mapper;

import com.films.filmsAts.dto.CommentResponseDTO;
import com.films.filmsAts.entity.Comment;

public class CommentMapper {

    public static CommentResponseDTO toResponse(Comment entity) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(entity.getId());
        dto.setText(entity.getText());
        dto.setUserId(entity.getUser().getId());
        dto.setUsername(entity.getUser().getUsername());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}