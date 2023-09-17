ALTER TABLE users ADD is_enabled BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE users SET is_enabled = true WHERE id <= 2;