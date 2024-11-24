package faang.school.postservice.service;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public Hashtag create(String tag) {
        Optional<Hashtag> hashtag = hashtagRepository.findByTag(tag);

        if (hashtag.isEmpty()) {
            Hashtag newHashtag = Hashtag
                    .builder()
                    .tag(tag)
                    .createdAt(LocalDateTime.now())
                    .build();

            return hashtagRepository.save(newHashtag);
        }

        return hashtag.get();
    }

    public Optional<Hashtag> findByTag(String tag) {
        return hashtagRepository.findByTag(tag);
    }

    public List<Hashtag> findAllByTags(List<String> hashtags) {
        return hashtagRepository.findAllByTagIn(hashtags);
    }
}
