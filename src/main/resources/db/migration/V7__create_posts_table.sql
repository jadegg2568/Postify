CREATE TABLE posts
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID                     NOT NULL UNIQUE,
    author_id  BIGINT                   NOT NULL,
    title      VARCHAR(255)             NOT NULL,
    content    TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_posts_uuid ON posts (uuid);
CREATE INDEX idx_posts_author_id ON posts (author_id);
