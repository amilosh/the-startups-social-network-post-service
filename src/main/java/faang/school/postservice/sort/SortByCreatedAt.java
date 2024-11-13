package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByCreatedAt extends SortBy {
    public SortByCreatedAt() {
        super(PostField.CREATEDAT);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(Post::getCreatedAt);
    }
}
