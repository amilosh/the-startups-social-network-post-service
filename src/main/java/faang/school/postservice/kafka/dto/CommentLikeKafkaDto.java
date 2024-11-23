package faang.school.postservice.kafka.dto;

import faang.school.postservice.dto.like.LikeAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeKafkaDto {
    private Long commentId;
    private LikeAction action;
}
