-- add column permissions with SMALLINT (-32767-32767)
-- we could also use TINYINT but postgres doesn't support it
ALTER TABLE users ADD COLUMN permissions SMALLINT;

-- update all users where it's null
UPDATE users SET permissions = 0 WHERE permissions IS NULL;

-- make non-null
ALTER TABLE users ALTER COLUMN permissions SET NOT NULL;
