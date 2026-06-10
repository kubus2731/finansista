-- =====================================================================
-- Skrypt generowania danych demo do testów wydajnościowych (SBD pkt 1 i 6)
-- URUCHAMIAĆ RĘCZNIE (SQLcl / SQL Developer / sqlplus) — NIE jest to
-- migracja Flyway i nie wykonuje się przy starcie aplikacji.
--
-- Zależności: dane słownikowe z V2 (użytkownicy 1-4, departamenty 1-15,
-- statusy 1-7, kategorie 1-2) muszą już istnieć.
--
-- Wariant A (zalecany, set-based): szybkie ładowanie dużych wolumenów.
-- Zmień próg w CONNECT BY na 1000 / 10000 / 100000 / 1000000.
-- =====================================================================

INSERT INTO requests (title, description, amount, id_u, id_rs, id_d, id_cc)
SELECT 'Wniosek testowy #' || LEVEL,
       'Dane wygenerowane do testów wydajnościowych',
       ROUND(DBMS_RANDOM.VALUE(100, 50000), 2),
       MOD(LEVEL, 4)  + 1,   -- id_u   : 1..4
       MOD(LEVEL, 7)  + 1,   -- id_rs  : 1..7
       MOD(LEVEL, 15) + 1,   -- id_d   : 1..15
       MOD(LEVEL, 2)  + 1    -- id_cc  : 1..2
FROM dual
CONNECT BY LEVEL <= 1000000;   -- <-- ZMIEŃ liczbę rekordów tutaj

COMMIT;

-- Po załadowaniu zaktualizuj statystyki, żeby optymalizator użył indeksów z V6:
EXEC DBMS_STATS.GATHER_TABLE_STATS(USER, 'REQUESTS');

-- =====================================================================
-- Czyszczenie danych testowych (zostawia dane słownikowe z V2):
--   DELETE FROM requests WHERE title LIKE 'Wniosek testowy #%';
--   COMMIT;
-- =====================================================================
