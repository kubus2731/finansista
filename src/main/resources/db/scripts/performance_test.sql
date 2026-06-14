SET TIMING ON
SET LINESIZE 200
SET PAGESIZE 50

PROMPT ================== SKALA DANYCH ==================
SELECT COUNT(*) AS wierszy_w_requests FROM requests;

PROMPT
PROMPT ===== Q1: Paginacja listy wniosków (strona 3 po 20, najnowsze) =====
SELECT id, title, amount, created_at
FROM requests
ORDER BY created_at DESC
OFFSET 40 ROWS FETCH NEXT 20 ROWS ONLY;

PROMPT
PROMPT ===== Q2: Wnioski wydziału, sortowane po dacie (ekran przeglądu) =====
SELECT COUNT(*) AS cnt
FROM (
    SELECT id
    FROM requests
    WHERE department_id = 1
    ORDER BY created_at DESC
    OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY
);

PROMPT
PROMPT ===== Q3: Raport agregowany per wydział (widok v_department_requests_summary) =====
SELECT * FROM v_department_requests_summary ORDER BY department_name;

PROMPT
PROMPT ===== Q4: Wyszukanie wniosku po external_id (wejście z API/linku) =====
SELECT id, title
FROM requests
WHERE external_id = (SELECT external_id FROM requests WHERE ROWNUM = 1);

PROMPT
PROMPT ===== PLAN: Q2 (filtr wydziału + sortowanie) =====
EXPLAIN PLAN FOR
SELECT id FROM requests WHERE department_id = 1
ORDER BY created_at DESC OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY;
SELECT plan_table_output FROM TABLE(DBMS_XPLAN.DISPLAY(NULL, NULL, 'BASIC +COST +ROWS'));

PROMPT
PROMPT ===== PLAN: Q4 (unikalny indeks external_id) =====
EXPLAIN PLAN FOR
SELECT id FROM requests WHERE external_id = HEXTORAW('00000000000000000000000000000001');
SELECT plan_table_output FROM TABLE(DBMS_XPLAN.DISPLAY(NULL, NULL, 'BASIC +COST +ROWS'));
