CREATE TABLE "contact_types" (
    "id" integer PRIMARY KEY,
    "type" varchar UNIQUE
);

CREATE TABLE "contact" (
    "id" integer PRIMARY KEY,
    "contact_type_id" integer,
    "first_name" varchar,
    "last_name" varchar,
    "address" varchar,
    "phone_number" varchar
);

CREATE TABLE "users" (
     "id" integer PRIMARY KEY,
     "email" varchar UNIQUE,
     "password" varchar,
     "first_name" varchar,
     "last_name" varchar,
     "contact_id" integer
);

ALTER TABLE "users" ADD FOREIGN KEY ("contact_id") REFERENCES "contact" ("id");

ALTER TABLE "contact" ADD FOREIGN KEY ("contact_type_id") REFERENCES "contact_types" ("id");