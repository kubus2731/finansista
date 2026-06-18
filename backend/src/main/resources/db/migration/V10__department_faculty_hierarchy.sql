-- Dziekanat należy do swojego wydziału. Dzięki temu Dziekan (konto w dziekanacie)
-- może obsługiwać wnioski składane na poziomie wydziału (środki wydziału, FACULTY_FUNDS),
-- bez wymagania, by wniosek był dosłownie w tym samym dziale co konto dziekana.
ALTER TABLE department ADD parent_department_id NUMBER(15);

ALTER TABLE department
    ADD CONSTRAINT fk_department_parent
    FOREIGN KEY (parent_department_id) REFERENCES department (id);

-- Dziekanat -> wydział (parent). Działy bez nadrzędnego (samorządy, kwestura, rektorat) zostają NULL.
UPDATE department SET parent_department_id = 1  WHERE id = 2;   -- Dziekanat WI -> Wydział Informatyki
UPDATE department SET parent_department_id = 3  WHERE id = 4;   -- Dziekanat WM -> Wydział Mechaniczny
UPDATE department SET parent_department_id = 5  WHERE id = 6;   -- Dziekanat WE -> Wydział Elektryczny
UPDATE department SET parent_department_id = 7  WHERE id = 8;   -- Dziekanat WA -> Wydział Architektury
UPDATE department SET parent_department_id = 9  WHERE id = 10;  -- Dziekanat WIZ -> Wydział Inżynierii Zarządzania
UPDATE department SET parent_department_id = 11 WHERE id = 12;  -- Dziekanat WBiNŚ -> Wydział Budownictwa i Nauk o Środowisku
