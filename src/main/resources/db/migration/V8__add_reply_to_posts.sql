ALTER TABLE posts
    ADD COLUMN reply_to_id BIGINT NULL;

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_reply_to
        FOREIGN KEY (reply_to_id) REFERENCES posts (id) ON DELETE SET NULL;

CREATE INDEX idx_posts_reply_to_id ON posts (reply_to_id);

