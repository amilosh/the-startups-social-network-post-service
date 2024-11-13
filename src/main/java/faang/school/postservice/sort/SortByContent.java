package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByContent extends SortBy {
    public SortByContent() {
        super(PostField.CONTENT);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(post -> post.getContent().length());
    }
}
