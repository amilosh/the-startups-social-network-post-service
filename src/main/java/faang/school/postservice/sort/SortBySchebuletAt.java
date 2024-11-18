package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortBySchebuletAt extends SortBy {
    public SortBySchebuletAt() {
        super(PostField.SCHEDULET_AT);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(Post::getScheduledAt);
    }
}
