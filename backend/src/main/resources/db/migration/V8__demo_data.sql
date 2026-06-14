INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9001, HEXTORAW('0190D1B2C3D47000A000000000009001'),'Demo', 'Admin', 'demo-admin@pb.edu.pl', '+48500000001',
       '{noop}demo123', 1, 1
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-admin@pb.edu.pl');

INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9002, HEXTORAW('0190D1B2C3D47000A000000000009002'),'Demo', 'Student', 'demo-student@pb.edu.pl', '+48500000002',
       '{noop}demo123', 2, 1
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-student@pb.edu.pl');

INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9003, HEXTORAW('0190D1B2C3D47000A000000000009003'),'Demo', 'Samorzad', 'demo-wrss@pb.edu.pl', '+48500000003',
       '{noop}demo123', 3, 13
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-wrss@pb.edu.pl');

INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9004, HEXTORAW('0190D1B2C3D47000A000000000009004'),
       'Demo', 'KomisjaPrawna', 'demo-komisja@pb.edu.pl', '+48500000004',
       '{noop}demo123', 5, 15
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-komisja@pb.edu.pl');

INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9005, HEXTORAW('0190D1B2C3D47000A000000000009005'),
       'Demo', 'Dziekanat', 'demo-dziekanat@pb.edu.pl', '+48500000005',
       '{noop}demo123', 6, 2
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-dziekanat@pb.edu.pl');

INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9006, HEXTORAW('0190D1B2C3D47000A000000000009006'),
       'Demo', 'Kwestura', 'demo-kwestura@pb.edu.pl', '+48500000006',
       '{noop}demo123', 7, 14
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-kwestura@pb.edu.pl');

INSERT INTO requests (id, external_id, title, description, amount, user_id, request_status_id, request_template_id, department_id, cost_category_id)
SELECT 9101, HEXTORAW('0190D1B2C3D47000B000000000009101'),'Demo: Juwenalia 2026','Wniosek demonstracyjny w statusie DRAFT — środki na organizację plenerowego koncertu Juwenaliów.',
       12500.00, 9002, 1, 1, 1, 1
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM requests WHERE external_id = HEXTORAW('0190D1B2C3D47000B000000000009101'));

INSERT INTO request_funding (request_id, funding_source_id, amount_requested, amount_granted)
SELECT 9101, 1, 12500.00, null FROM dual
WHERE NOT EXISTS (SELECT 1 FROM request_funding WHERE request_id = 9101 AND funding_source_id = 1);

INSERT INTO requests (id, external_id, title, description, amount, user_id, request_status_id, request_template_id, department_id, cost_category_id)
SELECT 9102, HEXTORAW('0190D1B2C3D47000B000000000009102'),'Demo: Konferencja kola naukowego (ZLOZONY)','Wniosek demonstracyjny w statusie SUBMITTED — wyjazd konferencyjny i materialy.',
4800.00, 9002, 2, 2, 1, 1
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM requests WHERE external_id = HEXTORAW('0190D1B2C3D47000B000000000009102'));

INSERT INTO request_funding (request_id, funding_source_id, amount_requested, amount_granted)
SELECT 9102, 3, 4800.00, null FROM dual
WHERE NOT EXISTS (SELECT 1 FROM request_funding WHERE request_id = 9102 AND funding_source_id = 3);

INSERT INTO requests (id, external_id, title, description, amount, user_id, request_status_id, request_template_id, department_id, cost_category_id)
SELECT 9103, HEXTORAW('0190D1B2C3D47000B000000000009103'),'Demo: Hackathon studencki','Wniosek demonstracyjny w statusie ACCEPTED — pula nagrod i catering podczas hackathonu.',
7600.00, 9003, 5, 1, 13, 2
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM requests WHERE external_id = HEXTORAW('0190D1B2C3D47000B000000000009103'));

INSERT INTO request_funding (request_id, funding_source_id, amount_requested, amount_granted)
SELECT 9103, 1, 7600.00, 7600.00 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM request_funding WHERE request_id = 9103 AND funding_source_id = 1);

COMMIT;
