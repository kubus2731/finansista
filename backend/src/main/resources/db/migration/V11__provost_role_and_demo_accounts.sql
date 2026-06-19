-- Rola prorektora: wpisuje opinię merytoryczną (Załącznik 1, sekcja III) jako osobny aktor obok CSSDiR.
INSERT INTO roles (id, name) VALUES (9, 'ROLE_PROVOST');

-- Brakujące konta demo, aby każdy etap obiegu miał dedykowane konto (hasło: demo123).
-- demo-cssdir   – ocena formalna (STUDENT_AFFAIRS)
-- demo-doktoranci – dysponent Samorządu Doktorantów (DOCTORAL_COUNCIL)
-- demo-prorektor – opinia prorektora (PROVOST)
INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9007, HEXTORAW('0190D1B2C3D47000A000000000009007'),
       'Demo', 'CSSDiR', 'demo-cssdir@pb.edu.pl', '+48500000007',
       '{noop}demo123', 8, 15
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-cssdir@pb.edu.pl');

INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9008, HEXTORAW('0190D1B2C3D47000A000000000009008'),
       'Demo', 'Doktoranci', 'demo-doktoranci@pb.edu.pl', '+48500000008',
       '{noop}demo123', 4, 16
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-doktoranci@pb.edu.pl');

INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
SELECT 9009, HEXTORAW('0190D1B2C3D47000A000000000009009'),
       'Demo', 'Prorektor', 'demo-prorektor@pb.edu.pl', '+48500000009',
       '{noop}demo123', 9, 15
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'demo-prorektor@pb.edu.pl');
