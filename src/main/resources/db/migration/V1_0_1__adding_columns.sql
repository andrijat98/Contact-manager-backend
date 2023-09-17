CREATE TABLE "roles" (
    id integer PRIMARY KEY,
    role_name VARCHAR(255)
);

ALTER TABLE "users" ADD "roles" integer;