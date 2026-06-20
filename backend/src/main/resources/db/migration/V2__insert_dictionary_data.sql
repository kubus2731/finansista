-- 1. Role w tabeli roles jako domyślne
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_STUDENT');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_STUDENT_COUNCIL');
INSERT INTO roles (id, name) VALUES (4, 'ROLE_DOCTORAL_COUNCIL');
INSERT INTO roles (id, name) VALUES (5, 'ROLE_LEGAL_COMMISSION');
INSERT INTO roles (id, name) VALUES (6, 'ROLE_DEAN_OFFICE');
INSERT INTO roles (id, name) VALUES (7, 'ROLE_FINANCE_OFFICE');
INSERT INTO roles (id, name) VALUES (8, 'ROLE_STUDENT_AFFAIRS');
INSERT INTO roles (id, name) VALUES (9, 'ROLE_PROVOST');

-- 2. Domyślne departamenty
INSERT INTO department (id, name) VALUES (1, 'Wydział Informatyki PB');
INSERT INTO department (id, name) VALUES (2, 'Dziekanat Wydziału Informatyki');
INSERT INTO department (id, name) VALUES (3, 'Wydział Mechaniczny PB');
INSERT INTO department (id, name) VALUES (4, 'Dziekanat Wydziału Mechanicznego');
INSERT INTO department (id, name) VALUES (5, 'Wydział Elektryczny PB');
INSERT INTO department (id, name) VALUES (6, 'Dziekanat Wydziału Elektrycznego');
INSERT INTO department (id, name) VALUES (7, 'Wydział Architektury PB');
INSERT INTO department (id, name) VALUES (8, 'Dziekanat Wydziału Architektury');
INSERT INTO department (id, name) VALUES (9, 'Wydział Inżynierii Zarządzania PB');
INSERT INTO department (id, name) VALUES (10, 'Dziekanat Wydziału Inżynierii Zarządzania');
INSERT INTO department (id, name) VALUES (11, 'Wydział Budownictwa i Nauk o Środowisku PB');
INSERT INTO department (id, name) VALUES (12, 'Dziekanat Wydziału Budownictwa i Nauk o Środowisku');
INSERT INTO department (id, name) VALUES (13, 'Samorząd Studentów PB');
INSERT INTO department (id, name) VALUES (14, 'Kwestura');
INSERT INTO department (id, name) VALUES (15, 'Rektorat');
INSERT INTO department (id, name) VALUES (16, 'Samorząd Doktorantów PB');

-- Dziekanat -> wydział (parent). Działy bez nadrzędnego (samorządy, kwestura, rektorat) zostają NULL.
UPDATE department SET parent_department_id = 1  WHERE id = 2;   -- Dziekanat WI -> Wydział Informatyki
UPDATE department SET parent_department_id = 3  WHERE id = 4;   -- Dziekanat WM -> Wydział Mechaniczny
UPDATE department SET parent_department_id = 5  WHERE id = 6;   -- Dziekanat WE -> Wydział Elektryczny
UPDATE department SET parent_department_id = 7  WHERE id = 8;   -- Dziekanat WA -> Wydział Architektury
UPDATE department SET parent_department_id = 9  WHERE id = 10;  -- Dziekanat WIZ -> Wydział Inżynierii Zarządzania
UPDATE department SET parent_department_id = 11 WHERE id = 12;  -- Dziekanat WBiNŚ -> Wydział Budownictwa i Nauk o Środowisku

-- 3. Statusy wniosków
INSERT INTO request_status (id, name) VALUES (1, 'DRAFT');
INSERT INTO request_status (id, name) VALUES (2, 'SUBMITTED');
INSERT INTO request_status (id, name) VALUES (3, 'FORMAL_EVALUATION');
INSERT INTO request_status (id, name) VALUES (4, 'UNDER_REVIEW');
INSERT INTO request_status (id, name) VALUES (5, 'ACCEPTED');
INSERT INTO request_status (id, name) VALUES (6, 'REJECTED');
INSERT INTO request_status (id, name) VALUES (7, 'CORRECTION_REQUIRED');

-- 4. Szablony wniosków
INSERT INTO request_templates (id, external_id, title, description, active)
VALUES (1, HEXTORAW('0190A1B2C3D47000A000000000000001'), 'Wniosek o dofinansowanie wydarzenia wydziałowego', 'Standardowy formularz zgłoszeniowy na organizację wydarzeń integracyjnych, kulturalnych i plenerowych dla studentów wydziału.', TRUE);

INSERT INTO request_templates (id, external_id, title, description, active)
VALUES (2, HEXTORAW('0190A1B2C3D47000A000000000000002'), 'Wniosek o dofinansowanie projektu koła naukowego', 'Formularz ubiegania się o środki na projekty inżynierskie, zakup podzespołów lub wyjazdy konferencyjne.', TRUE);

-- 5. Kategorie wniosków
INSERT INTO cost_category (id, name, description)
VALUES (1, 'Organizacja Wydarzeń Studenckich', 'Środki na logistykę, promocję oraz realizację imprez masowych i wydziałowych.');

INSERT INTO cost_category (id, name, description)
VALUES (2, 'Nagrody i Stypendia', 'Środki przeznaczone na pulę nagród w konkursach, wsparcie socjalne oraz stypendia rektora.');

-- 6. Źródła finansowania
INSERT INTO funding_source (id, name) VALUES (1, 'STUDENT_COUNCIL');
INSERT INTO funding_source (id, name) VALUES (2, 'DOCTORAL_COUNCIL');
INSERT INTO funding_source (id, name) VALUES (3, 'INITIATIVE_FUNDS');
INSERT INTO funding_source (id, name) VALUES (4, 'FACULTY_FUNDS');

COMMIT;