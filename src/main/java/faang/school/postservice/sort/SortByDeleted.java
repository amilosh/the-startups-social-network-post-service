package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByDeleted extends SortBy {
    public SortByDeleted() {
        super(PostField.DELETED);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(Post::isDeleted);
    }
}
