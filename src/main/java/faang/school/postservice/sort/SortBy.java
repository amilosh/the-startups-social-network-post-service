package faang.school.postservice.sort;

import faang.school.postservice.model.Post;

import java.util.Comparator;

public abstract class SortBy {
    private PostField postField;

    public SortBy(PostField postField) {
        this.postField = postField;
    }

    public abstract Comparator<Post> getComparator();

    public PostField getPostField() {
        return postField;
    }
}
