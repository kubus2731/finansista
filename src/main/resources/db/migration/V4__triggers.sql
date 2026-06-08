-- 1. Automatyczna aktualizacja czasu modyfikacji
CREATE OR REPLACE TRIGGER t_requests_update_now
    BEFORE UPDATE ON requests
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- 2. Trigger audytowy
CREATE OR REPLACE TRIGGER t_activity
    AFTER UPDATE OF id_rs ON requests
    FOR EACH ROW
    WHEN ( NEW.ID_RS != OLD.ID_RS )
BEGIN
    INSERT INTO activity_log( id_req, id_u, id_old_status, id_new_status, description)
    VALUES (:NEW.id_req,
            :NEW.id_u,
            :OLD.id_rs,
            :NEW.id_rs,
            'Automatyczna rejestracja zmiany statusu przez system');
END;
/

-- 3. Trigger blokujący zawartość wysłanych wniosków
CREATE OR REPLACE TRIGGER t_block
    BEFORE UPDATE ON requests
    FOR EACH ROW
BEGIN
    IF :OLD.id_rs NOT IN (1, 7) THEN
        IF :OLD.amount != :NEW.amount
            OR :OLD.title != :NEW.title
            OR DBMS_LOB.COMPARE(:OLD.description, :NEW.description) != 0
            OR :OLD.id_cc != :NEW.id_cc THEN
            RAISE_APPLICATION_ERROR(-20001, 'Błąd: Wniosek został już wysłany do weryfikacji. Trwa procedowanie, edycja zawartości jest zablokowana.');
        END IF;
    END IF;
END;
/