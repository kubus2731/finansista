-- =====================================================================
-- V4: Triggery na tabeli requests
-- Statusy (z V2): 1 DRAFT, 2 SUBMITTED, 3 FORMAL_EVALUATION,
-- 4 UNDER_REVIEW, 5 ACCEPTED, 6 REJECTED, 7 CORRECTION_REQUIRED.
-- (Maszyna stanów: patrz V7.)
-- =====================================================================

-- 1. Automatyczna aktualizacja czasu modyfikacji
CREATE OR REPLACE TRIGGER t_requests_update_now
    BEFORE UPDATE ON requests
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- 2. Trigger audytowy: zapisuje rzeczywistego autora zmiany statusu.
--    Aktora dostarcza pakiet finansista_pkg (ustawiany przez evaluate_request
--    lub bezpośrednio przez aplikację); awaryjnie loguje wnioskodawcę.
--    UWAGA: trigger odwołuje się do pakietu z V5 - po starcie Flyway
--    skompiluje się jako VALID dopiero po utworzeniu pakietu (V5). To poprawne.
CREATE OR REPLACE TRIGGER t_activity
    AFTER UPDATE OF id_rs ON requests
    FOR EACH ROW
    WHEN (NEW.id_rs != OLD.id_rs)
BEGIN
    INSERT INTO activity_log(id_req, id_u, id_old_status, id_new_status, description)
    VALUES (:NEW.id_req,
            NVL(finansista_pkg.get_actor, :NEW.id_u),
            :OLD.id_rs,
            :NEW.id_rs,
            'Zmiana statusu zarejestrowana automatycznie przez system');
END;
/

-- 3. Trigger blokujący edycję treści wysłanych wniosków (NULL-safe na description)
CREATE OR REPLACE TRIGGER t_block
    BEFORE UPDATE ON requests
    FOR EACH ROW
BEGIN
    IF :OLD.id_rs NOT IN (1, 7) THEN
        IF :OLD.amount != :NEW.amount
            OR :OLD.title != :NEW.title
            OR :OLD.id_cc != :NEW.id_cc
            OR DBMS_LOB.COMPARE(NVL(:OLD.description, EMPTY_CLOB()),
                                NVL(:NEW.description, EMPTY_CLOB())) != 0 THEN
            RAISE_APPLICATION_ERROR(-20001,
                'Błąd: Wniosek został już wysłany do weryfikacji. Trwa procedowanie, edycja zawartości jest zablokowana.');
        END IF;
    END IF;
END;
/
