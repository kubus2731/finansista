SET VERIFY OFF
SET SERVEROUTPUT ON

DECLARE
    v_target   PLS_INTEGER := &1;
    v_existing PLS_INTEGER;
    v_missing  PLS_INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_existing
    FROM requests
    WHERE title LIKE 'Wniosek testowy #%';

    v_missing := v_target - v_existing;

    IF v_missing > 0 THEN
        INSERT INTO requests
            (external_id, title, description, amount,
             user_id, request_status_id, department_id, cost_category_id)
        SELECT SYS_GUID(),
               'Wniosek testowy #' || (v_existing + seq),
               'Dane wygenerowane do testów wydajnościowych',
               ROUND(DBMS_RANDOM.VALUE(100, 50000), 2),
               MOD(seq, 4)  + 1,
               MOD(seq, 7)  + 1,
               MOD(seq, 15) + 1,
               MOD(seq, 2)  + 1
        FROM (SELECT ROWNUM AS seq
              FROM (SELECT LEVEL FROM dual CONNECT BY LEVEL <= 2000),
                   (SELECT LEVEL FROM dual CONNECT BY LEVEL <= 500)
              WHERE ROWNUM <= v_missing);
    END IF;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Dodano ' || GREATEST(v_missing, 0) ||
                         ' wierszy, stan testowych: ' || GREATEST(v_target, v_existing));
END;
/

EXEC DBMS_STATS.GATHER_TABLE_STATS(USER, 'REQUESTS');

SELECT COUNT(*) AS wierszy_w_requests FROM requests;

