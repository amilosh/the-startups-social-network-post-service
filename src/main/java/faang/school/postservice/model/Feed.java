package faang.school.postservice.model;

import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class Feed {
    private long userId;
    private LinkedHashSet<Long> postIds;
}
