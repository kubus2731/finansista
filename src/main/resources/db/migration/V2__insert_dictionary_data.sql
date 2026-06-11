-- 1. Role w tabeli roles jako domyślne
INSERT INTO roles (id, name) VALUES (HEXTORAW('00000000000000000000000000000001'), 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (HEXTORAW('00000000000000000000000000000002'), 'ROLE_STUDENT');
INSERT INTO roles (id, name) VALUES (HEXTORAW('00000000000000000000000000000003'), 'ROLE_WRSS');
INSERT INTO roles (id, name) VALUES (HEXTORAW('00000000000000000000000000000004'), 'ROLE_LEGAL_COMMISSION');
INSERT INTO roles (id, name) VALUES (HEXTORAW('00000000000000000000000000000005'), 'ROLE_DEAN_OFFICE');
INSERT INTO roles (id, name) VALUES (HEXTORAW('00000000000000000000000000000006'), 'ROLE_FINANCE_OFFICE');

-- 2. Domyślne departamenty
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000001'), 'Wydział Informatyki PB');
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000002'), 'Dziekanat Wydziału Informatyki');
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000003'), 'Wydział Mechaniczny PB');
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000004'), 'Dziekanat Wydziału Mechanicznego');
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000005'), 'Wydział Elektryczny PB');
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000006'), 'Dziekanat Wydziału Elektrycznego');
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000007'), 'Wydział Architektury PB');
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000008'), 'Dziekanat Wydziału Architektury');
INSERT INTO department (id, name) VALUES (HEXTORAW('00000000000000000000000000000009'), 'Wydział Inżynierii Zarządzania PB');
INSERT INTO department (id, name) VALUES (HEXTORAW('0000000000000000000000000000000a'), 'Dziekanat Wydziału Inżynierii Zarządzania');
INSERT INTO department (id, name) VALUES (HEXTORAW('0000000000000000000000000000000b'), 'Wydział Budownictwa i Nauk o Środowisku PB');
INSERT INTO department (id, name) VALUES (HEXTORAW('0000000000000000000000000000000c'), 'Dziekanat Wydziału Budownictwa i Nauk o Środowisku');
INSERT INTO department (id, name) VALUES (HEXTORAW('0000000000000000000000000000000d'), 'Samorząd Studentów PB');
INSERT INTO department (id, name) VALUES (HEXTORAW('0000000000000000000000000000000e'), 'Kwestura');
INSERT INTO department (id, name) VALUES (HEXTORAW('0000000000000000000000000000000f'), 'Rektorat');

-- 3. Statusy wniosków
INSERT INTO request_status (id, name) VALUES (HEXTORAW('00000000000000000000000000000001'), 'DRAFT');
INSERT INTO request_status (id, name) VALUES (HEXTORAW('00000000000000000000000000000002'), 'SUBMITTED');
INSERT INTO request_status (id, name) VALUES (HEXTORAW('00000000000000000000000000000003'), 'FORMAL_EVALUATION');
INSERT INTO request_status (id, name) VALUES (HEXTORAW('00000000000000000000000000000004'), 'UNDER_REVIEW');
INSERT INTO request_status (id, name) VALUES (HEXTORAW('00000000000000000000000000000005'), 'ACCEPTED');
INSERT INTO request_status (id, name) VALUES (HEXTORAW('00000000000000000000000000000006'), 'REJECTED');
INSERT INTO request_status (id, name) VALUES (HEXTORAW('00000000000000000000000000000007'), 'CORRECTION_REQUIRED');

-- 4. Szablony wniosków
INSERT INTO request_templates (id, title, description, active)
VALUES (HEXTORAW('00000000000000000000000000000001'), 'Wniosek o dofinansowanie wydarzenia wydziałowego', 'Standardowy formularz zgłoszeniowy na organizację wydarzeń integracyjnych, kulturalnych i plenerowych dla studentów wydziału.', TRUE);

INSERT INTO request_templates (id, title, description, active)
VALUES (HEXTORAW('00000000000000000000000000000002'), 'Wniosek o dofinansowanie projektu koła naukowego', 'Formularz ubiegania się o środki na projekty inżynierskie, zakup podzespołów lub wyjazdy konferencyjne.', TRUE);

-- 5. Kategorie wniosków
INSERT INTO cost_category (id, name, description)
VALUES (HEXTORAW('00000000000000000000000000000001'), 'Organizacja Wydarzeń Studenckich', 'Środki na logistykę, promocję oraz realizację imprez masowych i wydziałowych.');

INSERT INTO cost_category (id, name, description)
VALUES (HEXTORAW('00000000000000000000000000000002'), 'Nagrody i Stypendia', 'Środki przeznaczone na pulę nagród w konkursach, wsparcie socjalne oraz stypendia rektora.');

-- 6. Konta użytkowników do testów
INSERT INTO users (id, name, surname, email, phone_number, password, role_id, department_id)
VALUES (HEXTORAW('00000000000000000000000000000001'), 'Jakub', 'Matusiewicz', 'j.matusiewicz@student.pb.edu.pl', '500111222', '{noop}admin123', HEXTORAW('00000000000000000000000000000001'), HEXTORAW('00000000000000000000000000000003'));

INSERT INTO users (id, name, surname, email, phone_number, password, role_id, department_id)
VALUES (HEXTORAW('00000000000000000000000000000002'), 'Jakub', 'Borkowski', 'j.borkowski@student.pb.edu.pl', '500333444', '{noop}admin123', HEXTORAW('00000000000000000000000000000001'), HEXTORAW('00000000000000000000000000000001'));

-- Pracownik oceniający poprawność i zgodność
INSERT INTO users (id, name, surname, email, phone_number, password, role_id, department_id)
VALUES (HEXTORAW('00000000000000000000000000000003'), 'Anna', 'Zgodna', 'a.zgodna@pb.edu.pl', '857460001', '{noop}komisja123', HEXTORAW('00000000000000000000000000000004'), HEXTORAW('00000000000000000000000000000003'));

-- Testowy Student składający wnioski
INSERT INTO users (id, name, surname, email, phone_number, password, role_id, department_id)
VALUES (HEXTORAW('00000000000000000000000000000004'), 'Jan', 'Wnioskodawca', 'j.wnioskodawca@student.pb.edu.pl', '500999888', '{noop}student123', HEXTORAW('00000000000000000000000000000002'), HEXTORAW('00000000000000000000000000000001'));

COMMIT;