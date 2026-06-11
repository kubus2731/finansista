-- 1. Role w tabeli roles jako domyślne
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_STUDENT');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_WRSS');
INSERT INTO roles (id, name) VALUES (4, 'ROLE_LEGAL_COMMISSION');
INSERT INTO roles (id, name) VALUES (5, 'ROLE_DEAN_OFFICE');
INSERT INTO roles (id, name) VALUES (6, 'ROLE_FINANCE_OFFICE');

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

-- 3. Statusy wniosków
INSERT INTO request_status (id, name) VALUES (1, 'DRAFT');
INSERT INTO request_status (id, name) VALUES (2, 'SUBMITTED');
INSERT INTO request_status (id, name) VALUES (3, 'FORMAL_EVALUATION');
INSERT INTO request_status (id, name) VALUES (4, 'UNDER_REVIEW');
INSERT INTO request_status (id, name) VALUES (5, 'ACCEPTED');
INSERT INTO request_status (id, name) VALUES (6, 'REJECTED');
INSERT INTO request_status (id, name) VALUES (7, 'CORRECTION_REQUIRED');

-- 4. Szablony wniosków
INSERT INTO request_templates (id, title, description, active)
VALUES (1, 'Wniosek o dofinansowanie wydarzenia wydziałowego', 'Standardowy formularz zgłoszeniowy na organizację wydarzeń integracyjnych, kulturalnych i plenerowych dla studentów wydziału.', TRUE);

INSERT INTO request_templates (id, title, description, active)
VALUES (2, 'Wniosek o dofinansowanie projektu koła naukowego', 'Formularz ubiegania się o środki na projekty inżynierskie, zakup podzespołów lub wyjazdy konferencyjne.', TRUE);

-- 5. Kategorie wniosków
INSERT INTO cost_category (id, name, description)
VALUES (1, 'Organizacja Wydarzeń Studenckich', 'Środki na logistykę, promocję oraz realizację imprez masowych i wydziałowych.');

INSERT INTO cost_category (id, name, description)
VALUES (2, 'Nagrody i Stypendia', 'Środki przeznaczone na pulę nagród w konkursach, wsparcie socjalne oraz stypendia rektora.');

-- 6. Źródła finansowania
INSERT INTO funding_source (id, name) VALUES (1, 'Zarząd Samorządu Studentów');
INSERT INTO funding_source (id, name) VALUES (2, 'Rada Uczelniana');
INSERT INTO funding_source (id, name) VALUES (3, 'Dziekan Wydziału');
INSERT INTO funding_source (id, name) VALUES (4, 'Sponsor Prywatny');

-- 7. Konta użytkowników do testów
INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
VALUES (1, SYS_GUID(), 'Jakub', 'Matusiewicz', 'j.matusiewicz@student.pb.edu.pl', '500111222', '{noop}admin123', 1, 3);

INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
VALUES (2, SYS_GUID(), 'Jakub', 'Borkowski', 'j.borkowski@student.pb.edu.pl', '500333444', '{noop}admin123', 1, 1);

-- Pracownik oceniający poprawność i zgodność
INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
VALUES (3, SYS_GUID(), 'Anna', 'Zgodna', 'a.zgodna@pb.edu.pl', '857460001', '{noop}komisja123', 4, 3);

-- Testowy Student składający wnioski
INSERT INTO users (id, external_id, name, surname, email, phone_number, password, role_id, department_id)
VALUES (4, SYS_GUID(), 'Jan', 'Wnioskodawca', 'j.wnioskodawca@student.pb.edu.pl', '500999888', '{noop}student123', 2, 1);

COMMIT;