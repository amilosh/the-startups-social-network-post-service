package faang.school.postservice.model.dto.kafka;

import faang.school.postservice.model.enums.AuthorType;

public class PostDto {
    private String content;
    private Long authorId;
    private AuthorType authorType;
}