package faang.school.postservice.dto.news.feed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedEvent {

    @NotNull
    @Positive
    private Long postId;

    @NotNull
    private List<@NotNull Long> followerIds;
}
