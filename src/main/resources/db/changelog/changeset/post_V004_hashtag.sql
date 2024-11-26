CREATE TABLE IF NOT EXISTS hashtag (
    id BIGSERIAL PRIMARY KEY,
    tag VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS post_hashtags (
    post_id BIGINT NOT NULL,
    hashtag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, hashtag_id),
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    FOREIGN KEY (hashtag_id) REFERENCES hashtag(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_post_id ON post_hashtags(post_id);
CREATE INDEX IF NOT EXISTS idx_hashtag_id ON post_hashtags(hashtag_id);
CREATE INDEX IF NOT EXISTS idx_post_hashtag ON post_hashtags(post_id, hashtag_id);