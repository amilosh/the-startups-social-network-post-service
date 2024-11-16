package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByPublishedAt extends SortBy {
    public SortByPublishedAt() {
        super(PostField.PUBLISHED_AT);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(Post::getPublishedAt);
    }
}
