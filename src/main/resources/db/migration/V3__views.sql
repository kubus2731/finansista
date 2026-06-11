-- 1. Widok agregujący statystyki wniosków dla konkretnych wydziałów
CREATE OR REPLACE VIEW v_department_requests_summary AS
SELECT
    d.id,
    d.name AS department_name,
    COUNT(r.id) AS total_requests_count,
    NVL(SUM(r.amount), 0) AS total_requests_amount,
    SUM(CASE WHEN r.request_status_id = HEXTORAW('00000000000000000000000000000005') THEN 1 ELSE 0 END) AS accepted_requests_count,
    NVL(SUM(CASE WHEN r.request_status_id = HEXTORAW('00000000000000000000000000000005') THEN r.amount ELSE 0 END), 0) AS accepted_requests_amount,
    SUM(CASE WHEN r.request_status_id = HEXTORAW('00000000000000000000000000000006') THEN 1 ELSE 0 END) AS rejected_requests_count,
    NVL(SUM(CASE WHEN r.request_status_id = HEXTORAW('00000000000000000000000000000006') THEN r.amount ELSE 0 END), 0) AS rejected_requests_amount
FROM department d
         LEFT JOIN requests r ON d.id = r.department_id
GROUP BY d.id, d.name;


-- 2. Widok ze szczegółami wniosków
CREATE OR REPLACE VIEW v_request_details AS
SELECT
    r.id AS request_id,
    r.title AS request_title,
    r.amount AS request_amount,
    u.name || ' ' || u.surname AS applicant_name,
    d.name AS department_name,
    cc.name AS cost_category_name,
    rs.name AS status_name,
    r.created_at,
    r.updated_at
FROM requests r
         JOIN users u ON r.user_id = u.id
         JOIN department d ON r.department_id = d.id
         JOIN cost_category cc ON r.cost_category_id = cc.id
         JOIN request_status rs ON r.request_status_id = rs.id;