CREATE TABLE messages (
                          id BIGSERIAL PRIMARY KEY,
                          uuid UUID NOT NULL UNIQUE,
                          dialogue_id BIGINT NOT NULL,
                          sender_id BIGINT NOT NULL,
                          text TEXT NOT NULL,
                          reply_to_id BIGINT,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                          CONSTRAINT fk_messages_dialogue FOREIGN KEY (dialogue_id) REFERENCES dialogues (id) ON DELETE CASCADE,
                          CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE,

                          CONSTRAINT fk_messages_reply_to FOREIGN KEY (reply_to_id) REFERENCES messages (id) ON DELETE SET NULL
);

-- Твой составной индекс для быстрого скролла чата (выборка по диалогу + сортировка по дате)
CREATE INDEX idx_messages_dialogue_created ON messages (dialogue_id, created_at);