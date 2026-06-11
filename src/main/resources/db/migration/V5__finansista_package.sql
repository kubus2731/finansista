-- Pakiet
CREATE OR REPLACE PACKAGE finansista_pkg AS
    -- Funkcja: Wylicza łączną kwotę zaakceptowanych wniosków dla podanego wydziału
    FUNCTION department_used_budget(p_dept_id IN RAW) RETURN NUMBER;

    -- Procedura: Kompleksowa obsługa oceny wniosku przez jednostki nadrzędne
    PROCEDURE evaluate_request(
        p_req_id IN RAW,
        p_evaluator_id IN RAW,
        p_new_status_id IN RAW,
        p_comment_content IN CLOB
    );
END finansista_pkg;
/

-- Ciało pakietu
CREATE OR REPLACE PACKAGE BODY finansista_pkg AS

    FUNCTION department_used_budget(p_dept_id IN RAW) RETURN NUMBER IS
        v_total_amount NUMBER(15,2);
    BEGIN
        SELECT NVL(SUM(amount), 0)
        INTO v_total_amount
        FROM requests
        WHERE requests.department_id = p_dept_id AND requests.request_status_id = HEXTORAW('00000000000000000000000000000005');

        RETURN v_total_amount;
    END department_used_budget;

    PROCEDURE evaluate_request(
        p_req_id IN RAW,
        p_evaluator_id IN RAW,
        p_new_status_id IN RAW,
        p_comment_content IN CLOB
    ) IS
        v_current_status RAW(16);
    BEGIN
        SELECT request_status_id INTO v_current_status FROM requests WHERE id = p_req_id;
            IF v_current_status NOT IN (HEXTORAW('00000000000000000000000000000002'), HEXTORAW('00000000000000000000000000000003'), HEXTORAW('00000000000000000000000000000004')) THEN
                RAISE_APPLICATION_ERROR(-20002, 'Błąd: Wniosek nie jest na etapie weryfikacji. Nie można go ocenić.');
            END IF;
        UPDATE requests
        SET request_status_id = p_new_status_id
        WHERE id = p_req_id;
            IF p_comment_content IS NOT NULL THEN
                INSERT INTO comments (request_id, user_id, content)
                VALUES (p_req_id, p_evaluator_id, p_comment_content);
            END IF;

        -- UWAGA: Brak instrukcji COMMIT, zatwierdzanie transakcji po stronie aplikacji (Spring Boot @Transactional),
        -- do zrobienia
    END evaluate_request;

END finansista_pkg;
/