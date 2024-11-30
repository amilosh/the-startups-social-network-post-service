CREATE TABLE view (
    id                          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id                     bigint NOT NULL UNIQUE,
    view_count                  bigint,
    version                     bigint,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);