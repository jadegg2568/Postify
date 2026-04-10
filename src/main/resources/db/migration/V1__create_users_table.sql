CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    uuid          UUID         NOT NULL UNIQUE,
    mail          varchar(255) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(24)  NOT NULL UNIQUE,
    title         VARCHAR(32),
    description   VARCHAR(256),
    photo_key     VARCHAR(255),
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_uuid ON users(uuid);
CREATE INDEX idx_users_mail ON users(mail);
CREATE INDEX idx_users_name ON users(name);