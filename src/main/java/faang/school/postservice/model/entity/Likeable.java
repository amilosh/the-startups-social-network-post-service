package faang.school.postservice.model.entity;

import java.util.List;

public interface Likeable {
    long getId();

    List<Like> getLikes();
}
