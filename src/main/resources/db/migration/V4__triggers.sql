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
    AFTER UPDATE OF request_status_id ON requests
    FOR EACH ROW
    WHEN ( NEW.request_status_id != OLD.request_status_id )
BEGIN
    INSERT INTO activity_log(id, user_id, old_status_id, new_status_id, description)
    VALUES (:NEW.id,
            :NEW.user_id,
            :OLD.request_status_id,
            :NEW.request_status_id,
            'Automatyczna rejestracja zmiany statusu przez system');
END;
/

-- 3. Trigger blokujący zawartość wysłanych wniosków
CREATE OR REPLACE TRIGGER t_block
    BEFORE UPDATE ON requests
    FOR EACH ROW
BEGIN
    IF :OLD.request_status_id NOT IN (HEXTORAW('00000000000000000000000000000001'), HEXTORAW('00000000000000000000000000000007')) THEN
        IF :OLD.amount != :NEW.amount
            OR :OLD.title != :NEW.title
            OR DBMS_LOB.COMPARE(:OLD.description, :NEW.description) != 0
            OR :OLD.cost_category_id != :NEW.cost_category_id THEN
            RAISE_APPLICATION_ERROR(-20001, 'Błąd: Wniosek został już wysłany do weryfikacji. Trwa procedowanie, edycja zawartości jest zablokowana.');
        END IF;
    END IF;
END;
/