CREATE TABLE comment_likes (
    id         BIGSERIAL PRIMARY KEY,
    comment_id BIGINT    NOT NULL,
    amount     INT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_comment_likes_comment_id FOREIGN KEY (comment_id) REFERENCES comment (id)
);

CREATE TABLE post_likes (
    id      BIGSERIAL PRIMARY KEY,
    post_id BIGINT    NOT NULL,
    amount  INT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_post_likes_post_id FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE TABLE post_views (
    id      BIGSERIAL PRIMARY KEY,
    post_id BIGINT    NOT NULL,
    amount  INT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_post_views_post_id FOREIGN KEY (post_id) REFERENCES post (id)
);