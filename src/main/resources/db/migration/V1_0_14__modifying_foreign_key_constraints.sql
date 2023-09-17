ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS user_roles_user_id_fkey;
ALTER TABLE user_roles ADD CONSTRAINT user_roles_user_id_fkey
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE account_verifications DROP CONSTRAINT IF EXISTS account_verifications_user_tsid_fkey;
ALTER TABLE account_verifications DROP CONSTRAINT IF EXISTS account_verifications_user_id_fkey;
ALTER TABLE account_verifications ADD CONSTRAINT account_verifications_user_id_fkey
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE;

