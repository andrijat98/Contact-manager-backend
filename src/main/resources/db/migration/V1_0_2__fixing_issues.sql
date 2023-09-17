ALTER TABLE "contact" ADD "app_user_id" integer;
ALTER TABLE "contact" ADD FOREIGN KEY ("app_user_id") REFERENCES "users" ("id");

CREATE TABLE "user_roles" ("role_id" integer, "user_id" integer);
ALTER TABLE "user_roles" ADD FOREIGN KEY ("user_id") REFERENCES  "users" (id);
ALTER TABLE "user_roles" ADD FOREIGN KEY ("role_id") REFERENCES  "roles" (id);

ALTER TABLE "users" DROP COLUMN "contact_id";
ALTER TABLE "users" DROP COLUMN "roles";

CREATE SEQUENCE "contact_seq" START 1;
CREATE SEQUENCE "users_seq" START 1;
CREATE SEQUENCE "roles_seq" START 1;
CREATE SEQUENCE "contact_types_seq" START 1;