CREATE TABLE account_verifications(
    id BIGSERIAL PRIMARY KEY,
    user_tsid BIGINT NOT NULL REFERENCES "app-db".users (id),
    verification_url VARCHAR(255),
    date TIMESTAMP
)