ALTER TABLE posts
    ADD COLUMN views BIGINT NOT NULL DEFAULT 0;

CREATE TABLE post_views
(
    post_id    BIGINT                   NOT NULL,
    user_id    BIGINT                   NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (post_id, user_id),
    CONSTRAINT fk_post_views_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_views_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_post_views_user_id ON post_views (user_id);
CREATE INDEX idx_post_views_created_at ON post_views (created_at);
