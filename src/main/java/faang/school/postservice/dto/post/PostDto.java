package faang.school.postservice.dto.post;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;

import java.util.List;

public record PostDto(String content,
                      Long userId,
                      Long projectId) {
}
