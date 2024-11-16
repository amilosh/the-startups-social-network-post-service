package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByUpdatedAt extends SortBy {
    public SortByUpdatedAt() {
        super(PostField.UPDATED_AT);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(Post::getUpdatedAt);
    }
}
