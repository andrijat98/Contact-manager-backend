INSERT INTO "app-db".roles (role_name, tsid) VALUES ('ROLE_USER', 453899597133387633);
INSERT INTO "app-db".roles (role_name, tsid) VALUES ('ROLE_ADMIN', 454165907464597904);

INSERT INTO "app-db".users(email, password, first_name, last_name, tsid)
VALUES (
        'regularuser1@gmail.com',
        '$2a$12$5Ig5.jV2WMkBdEs23Or5HeGJIYV1eN/xASEqwU3XWqgXReLOt9Y7G',
        'Regular',
        'User',
        454165907150024959
);

INSERT INTO "app-db".users(email, password, first_name, last_name, tsid)
VALUES (
        'adminuser1@gmail.com',
        '$2a$12$injpKzh0b5buQ5grnJiqyuu1sfDXL367kqpTeoBo9j0YL7iGZwQ2u',
        'Admin',
        'User',
        453899832266071156
);

INSERT INTO "app-db".user_roles(role_id, user_id) VALUES (1, 1);
INSERT INTO "app-db".user_roles(role_id, user_id) VALUES (2, 2);