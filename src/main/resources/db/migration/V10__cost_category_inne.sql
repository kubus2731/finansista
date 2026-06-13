-- V10: kategoria "Inne" + miejsce na jej opis.
-- Sztywne 2 kategorie bywały zbyt wąskie; "Inne" + pole tekstowe daje elastyczność
-- (ten sam wzór co projectKind/scope/nature z polem _other).
INSERT INTO cost_category (id, name, description)
VALUES (3, 'Inne', 'Kategoria niestandardowa - doprecyzowana w polu opisowym wniosku.');

ALTER TABLE requests ADD (cost_category_other VARCHAR2(200));

COMMIT;
