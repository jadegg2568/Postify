ALTER TABLE sessions
    ADD COLUMN browser VARCHAR(255),
    ADD COLUMN os VARCHAR(255);

UPDATE sessions
SET browser = COALESCE(NULLIF(TRIM(title), ''), 'Unknown'),
    os = 'Unknown'
WHERE browser IS NULL OR os IS NULL;

ALTER TABLE sessions
    ALTER COLUMN browser SET NOT NULL,
    ALTER COLUMN os SET NOT NULL;

ALTER TABLE sessions
    DROP COLUMN title;
