package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByPublished extends SortBy {
    public SortByPublished() {
        super(PostField.PUBLISHED);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(Post::isPublished);
    }
}
