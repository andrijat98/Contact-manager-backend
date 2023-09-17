ALTER TABLE users ADD is_phone_verified BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD phone_number VARCHAR(15);

CREATE TABLE phone_verifications(
    id BIGSERIAL PRIMARY KEY,
    user_tsid BIGINT NOT NULL REFERENCES "app-db".users (id),
    verification_code VARCHAR(6),
    date TIMESTAMP
)