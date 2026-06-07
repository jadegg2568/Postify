-- V10__create_dialogues_and_messages.sql
-- PostgreSQL
CREATE TABLE dialogues (
   id BIGSERIAL PRIMARY KEY,
   uuid UUID NOT NULL UNIQUE,
   user1_id BIGINT NOT NULL REFERENCES users(id),
   user2_id BIGINT NOT NULL REFERENCES users(id),
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
   CONSTRAINT dialogues_unique_users UNIQUE (user1_id, user2_id)
);

CREATE INDEX idx_dialogues_user1 ON dialogues (user1_id);
CREATE INDEX idx_dialogues_user2 ON dialogues (user2_id);

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    dialogue_id BIGINT NOT NULL REFERENCES dialogues(id) ON DELETE CASCADE,
    sender_id BIGINT NOT NULL REFERENCES users(id),
    text TEXT NOT NULL,
    reply_to_id BIGINT NULL REFERENCES messages(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_messages_dialogue_created ON messages (dialogue_id, created_at);