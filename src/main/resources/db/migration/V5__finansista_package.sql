-- Pakiet
CREATE OR REPLACE PACKAGE finansista_pkg AS
    -- Funkcja: Wylicza łączną kwotę zaakceptowanych wniosków dla podanego wydziału
    FUNCTION department_used_budget(p_dept_id IN NUMBER) RETURN NUMBER;

    -- Procedura: Kompleksowa obsługa oceny wniosku przez jednostki nadrzędne
    PROCEDURE evaluate_request(
        p_req_id IN NUMBER,
        p_evaluator_id IN NUMBER,
        p_new_status_id IN NUMBER,
        p_comment_content IN CLOB
    );
END finansista_pkg;
/

-- Ciało pakietu
CREATE OR REPLACE PACKAGE BODY finansista_pkg AS

    FUNCTION department_used_budget(p_dept_id IN NUMBER) RETURN NUMBER IS
        v_total_amount NUMBER(15,2);
    BEGIN
        SELECT NVL(SUM(amount), 0)
        INTO v_total_amount
        FROM requests
        WHERE id_d = p_dept_id AND id_rs = 5;

        RETURN v_total_amount;
    END department_used_budget;

    PROCEDURE evaluate_request(
        p_req_id IN NUMBER,
        p_evaluator_id IN NUMBER,
        p_new_status_id IN NUMBER,
        p_comment_content IN CLOB
    ) IS
        v_current_status NUMBER;
    BEGIN
        SELECT id_rs INTO v_current_status FROM requests WHERE id_req = p_req_id;
            IF v_current_status NOT IN (2, 3, 4) THEN
                RAISE_APPLICATION_ERROR(-20002, 'Błąd: Wniosek nie jest na etapie weryfikacji. Nie można go ocenić.');
            END IF;
        UPDATE requests
        SET id_rs = p_new_status_id
        WHERE id_req = p_req_id;
            IF p_comment_content IS NOT NULL THEN
                INSERT INTO comments (id_req, id_u, content)
                VALUES (p_req_id, p_evaluator_id, p_comment_content);
            END IF;

        -- UWAGA: Brak instrukcji COMMIT, zatwierdzanie transakcji po stronie aplikacji (Spring Boot @Transactional),
        -- do zrobienia
    END evaluate_request;

END finansista_pkg;
/