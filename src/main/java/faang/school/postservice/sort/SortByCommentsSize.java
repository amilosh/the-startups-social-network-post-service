package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByCommentsSize extends SortBy {
    public SortByCommentsSize() {
        super(PostField.COMMENTS);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(post -> post.getComments().size());
    }
}
