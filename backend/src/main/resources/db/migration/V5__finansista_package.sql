-- Pakiet finansista_pkg
--   - śledzenie aktora (kto wykonuje zmianę) na potrzeby audytu w t_activity
--   - funkcja pola pochodnego: wykorzystany budżet wydziału
--   - procedura oceny wniosku (przejście statusu + komentarz)

-- Specyfikacja pakietu
CREATE OR REPLACE PACKAGE finansista_pkg AS
    -- Ustawienie / odczyt aktora bieżącej sesji (dla triggera audytowego)
    PROCEDURE set_actor(p_user_id IN NUMBER);
    FUNCTION  get_actor RETURN NUMBER;

    -- Funkcja: łączna kwota zaakceptowanych wniosków danego wydziału
    FUNCTION department_used_budget(p_dept_id IN NUMBER) RETURN NUMBER;

    -- Procedura: ocena wniosku przez jednostkę nadrzędną
    PROCEDURE evaluate_request(
        p_req_id          IN NUMBER,
        p_evaluator_id    IN NUMBER,
        p_new_status_id   IN NUMBER,
        p_comment_content IN CLOB
    );
END finansista_pkg;
/

-- Ciało pakietu
CREATE OR REPLACE PACKAGE BODY finansista_pkg AS

    -- Stan pakietu jest per-sesja: aplikacja ustawia aktora przed UPDATE,
    -- a trigger t_activity (V4) odczytuje go przy zapisie do activity_log.
    g_actor_id NUMBER(16);

    PROCEDURE set_actor(p_user_id IN NUMBER) IS
    BEGIN
        g_actor_id := p_user_id;
    END set_actor;

    FUNCTION get_actor RETURN NUMBER IS
    BEGIN
        RETURN g_actor_id;
    END get_actor;

    FUNCTION department_used_budget(p_dept_id IN NUMBER) RETURN NUMBER IS
        v_total_amount NUMBER(15,2);
    BEGIN
        SELECT NVL(SUM(amount), 0)
        INTO v_total_amount
        FROM requests
        WHERE department_id = p_dept_id AND request_status_id = 5;

        RETURN v_total_amount;
    END department_used_budget;

    PROCEDURE evaluate_request(
        p_req_id          IN NUMBER,
        p_evaluator_id    IN NUMBER,
        p_new_status_id   IN NUMBER,
        p_comment_content IN CLOB
    ) IS
        v_current_status NUMBER(16);
    BEGIN
        -- ustaw aktora, aby trigger audytowy zapisał właściwego użytkownika
        set_actor(p_evaluator_id);

        SELECT request_status_id INTO v_current_status
        FROM requests
        WHERE id = p_req_id
        FOR UPDATE;

        IF v_current_status NOT IN (2, 3, 4) THEN
            RAISE_APPLICATION_ERROR(-20003, 'Błąd: Wniosek nie jest na etapie weryfikacji. Nie można go ocenić.');
        END IF;

        UPDATE requests
        SET request_status_id = p_new_status_id
        WHERE id = p_req_id;

        IF p_comment_content IS NOT NULL THEN
            INSERT INTO comments (request_id, user_id, content)
            VALUES (p_req_id, p_evaluator_id, p_comment_content);
        END IF;

        -- Brak COMMIT: transakcję zatwierdza warstwa aplikacji (Spring @Transactional).
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20004, 'Błąd: Wniosek o podanym identyfikatorze nie istnieje.');
    END evaluate_request;

END finansista_pkg;
/
