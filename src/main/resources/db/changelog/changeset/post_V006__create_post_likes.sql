CREATE TABLE post_likes (
    id      BIGSERIAL PRIMARY KEY,
    post_id BIGINT    NOT NULL,
    amount  INT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_post_likes_post_id FOREIGN KEY (post_id) REFERENCES post (id)
);