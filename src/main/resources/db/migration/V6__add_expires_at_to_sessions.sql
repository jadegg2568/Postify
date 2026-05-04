ALTER TABLE sessions
    ADD COLUMN expires_at TIMESTAMPTZ;

-- set 30 days difference after created_at to old fields
UPDATE sessions
SET expires_at = created_at + INTERVAL '30 days'
WHERE expires_at IS NULL;

-- not null
ALTER TABLE sessions
    ALTER COLUMN expires_at SET NOT NULL;

-- index for deleting expired sessions
CREATE INDEX idx_sessions_expires_at ON sessions(expires_at);