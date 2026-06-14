-- 1. Automatyczna aktualizacja czasu modyfikacji
CREATE OR REPLACE TRIGGER t_requests_update_now
    BEFORE UPDATE ON requests
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- 2. Trigger audytowy: zapisuje rzeczywistego autora zmiany statusu.
CREATE OR REPLACE TRIGGER t_activity
    AFTER UPDATE OF request_status_id ON requests
    FOR EACH ROW
    WHEN (NEW.request_status_id != OLD.request_status_id)
BEGIN
    INSERT INTO activity_log(request_id, user_id, old_status_id, new_status_id, description)
    VALUES (:NEW.id,
            NVL(finansista_pkg.get_actor, :NEW.user_id),
            :OLD.request_status_id,
            :NEW.request_status_id,
            'Zmiana statusu zarejestrowana automatycznie przez system');
END;
/

-- 3. Trigger blokujący edycję treści wysłanych wniosków (NULL-safe na description)
CREATE OR REPLACE TRIGGER t_block
    BEFORE UPDATE ON requests
    FOR EACH ROW
BEGIN
    IF :OLD.request_status_id NOT IN (1, 7) THEN
        IF :OLD.amount != :NEW.amount
            OR :OLD.title != :NEW.title
            OR :OLD.cost_category_id != :NEW.cost_category_id
            OR DBMS_LOB.COMPARE(NVL(:OLD.description, EMPTY_CLOB()),
                                NVL(:NEW.description, EMPTY_CLOB())) != 0 THEN
            RAISE_APPLICATION_ERROR(-20001,
                'Błąd: Wniosek został już wysłany do weryfikacji. Trwa procedowanie, edycja zawartości jest zablokowana.');
        END IF;
    END IF;
END;
/
