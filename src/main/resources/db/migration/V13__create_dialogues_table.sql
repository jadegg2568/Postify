CREATE TABLE dialogues (
                           id BIGSERIAL PRIMARY KEY,
                           uuid UUID NOT NULL UNIQUE,
                           user1_id BIGINT NOT NULL,
                           user2_id BIGINT NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                           CONSTRAINT uq_dialogues_user1_user2 UNIQUE (user1_id, user2_id),

                           CONSTRAINT fk_dialogues_user1 FOREIGN KEY (user1_id) REFERENCES users (id) ON DELETE CASCADE,
                           CONSTRAINT fk_dialogues_user2 FOREIGN KEY (user2_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_dialogues_user1 ON dialogues(user1_id);
CREATE INDEX idx_dialogues_user2 ON dialogues(user2_id);
