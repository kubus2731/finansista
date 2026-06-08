-- 1. Widok agregujący statystyki wniosków dla konkretnych wydziałów
CREATE OR REPLACE VIEW v_department_requests_summary AS
SELECT
    d.id_d,
    d.d_name AS department_name,
    COUNT(r.id_req) AS total_requests_count,
    NVL(SUM(r.amount), 0) AS total_requests_amount,
    SUM(CASE WHEN r.id_rs = 5 THEN 1 ELSE 0 END) AS accepted_requests_count,
    NVL(SUM(CASE WHEN r.id_rs = 5 THEN r.amount ELSE 0 END), 0) AS accepted_requests_amount,
    SUM(CASE WHEN r.id_rs = 6 THEN 1 ELSE 0 END) AS rejected_requests_count,
    NVL(SUM(CASE WHEN r.id_rs = 6 THEN r.amount ELSE 0 END), 0) AS rejected_requests_amount
FROM department d
         LEFT JOIN requests r ON d.id_d = r.id_d
GROUP BY d.id_d, d.d_name;


-- 2. Widok ze szczegółami wniosków
CREATE OR REPLACE VIEW v_request_details AS
SELECT
    r.id_req,
    r.title AS request_title,
    r.amount AS request_amount,
    u.name || ' ' || u.surname AS applicant_name,
    d.d_name AS department_name,
    cc.name AS cost_category_name,
    rs.name AS status_name,
    r.created_at,
    r.updated_at
FROM requests r
         JOIN users u ON r.id_u = u.id_u
         JOIN department d ON r.id_d = d.id_d
         JOIN cost_category cc ON r.id_cc = cc.id_cc
         JOIN request_status rs ON r.id_rs = rs.id_rs;