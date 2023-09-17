ALTER TABLE "users" ALTER COLUMN "id" SET DEFAULT nextval('users_seq');
ALTER TABLE "contact" ALTER COLUMN "id" SET DEFAULT nextval('contact_seq');
ALTER TABLE "contact_types" ALTER COLUMN "id" SET DEFAULT nextval('contact_types_seq');
ALTER TABLE "roles" ALTER COLUMN "id" SET DEFAULT nextval('roles_seq');