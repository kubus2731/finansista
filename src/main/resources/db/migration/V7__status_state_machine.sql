-- V7: Maszyna stanów wniosku
-- Funkcja walidująca przejścia statusu + trigger ją egzekwujący.

-- Funkcja: czy przejście ze statusu p_old do p_new jest dozwolone (1 = tak, 0 = nie)
CREATE OR REPLACE FUNCTION is_valid_status_transition(
    p_old_status IN NUMBER,
    p_new_status IN NUMBER
) RETURN NUMBER
    DETERMINISTIC
IS
BEGIN
    IF p_old_status = p_new_status THEN
        RETURN 1;
    END IF;

    RETURN CASE
        WHEN p_old_status = 1 AND p_new_status = 2 THEN 1
        WHEN p_old_status = 2 AND p_new_status IN (3, 6, 7) THEN 1
        WHEN p_old_status = 3 AND p_new_status IN (4, 6, 7) THEN 1
        WHEN p_old_status = 4 AND p_new_status IN (5, 6, 7) THEN 1
        WHEN p_old_status = 7 AND p_new_status IN (1, 2) THEN 1
        ELSE 0
    END;
END;
/

-- Trigger, który blokuje niedozwolone przejścia statusu
CREATE OR REPLACE TRIGGER t_status_transition
    BEFORE UPDATE OF request_status_id ON requests
    FOR EACH ROW
    WHEN (NEW.request_status_id != OLD.request_status_id)
BEGIN
    IF is_valid_status_transition(:OLD.request_status_id, :NEW.request_status_id) = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Niedozwolone przejście statusu wniosku.');
    END IF;
END;
/
