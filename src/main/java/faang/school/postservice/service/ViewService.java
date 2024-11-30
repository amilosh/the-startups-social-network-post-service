package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.ViewEntity;
import faang.school.postservice.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final ViewRepository viewRepository;

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class)
    public void upsertView(Long postId, Long viewCount) {
        ViewEntity viewEntity = viewRepository.findByPostId(postId)
                .map(existingEntity -> {
                    existingEntity.setViewCount(existingEntity.getViewCount() + viewCount);
                    return existingEntity;
                })
                .orElseGet(() -> {
                    ViewEntity newEntity = new ViewEntity();
                    Post post = new Post();
                    post.setId(postId);
                    newEntity.setPost(post);
                    newEntity.setViewCount(viewCount);
                    return newEntity;
                });

        viewRepository.save(viewEntity);
    }
}
