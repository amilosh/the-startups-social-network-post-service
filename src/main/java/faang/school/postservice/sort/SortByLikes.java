package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByLikes extends SortBy {
    public SortByLikes() {
        super(PostField.LIKES);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(post -> post.getLikes().size());
    }

}
