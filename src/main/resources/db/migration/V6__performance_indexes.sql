-- Indeksy pod najczęstsze filtry/joiny na tabeli transakcyjnej (requests):
CREATE INDEX idx_requests_dept ON requests(department_id);
CREATE INDEX idx_requests_status ON requests(request_status_id);
CREATE INDEX idx_requests_user ON requests(user_id);
CREATE INDEX idx_requests_cost_cat ON requests(cost_category_id);
CREATE INDEX idx_requests_created_at ON requests(created_at);

-- Indeks złożony pod listy filtrowane po wydziale i sortowane po dacie
CREATE INDEX idx_requests_dept_created ON requests(department_id, created_at);

-- Tabele powiązane pobierane zawsze per wniosek:
CREATE INDEX idx_activity_request ON activity_log(request_id);
CREATE INDEX idx_activity_user ON activity_log(user_id);
CREATE INDEX idx_comments_request ON comments(request_id);
CREATE INDEX idx_attachments_request ON attachments(request_id);

ALTER TABLE requests ADD CONSTRAINT chk_requests_amount_positive CHECK (amount > 0);

-- Wskazówka do testów wydajnościowych:
--   EXEC DBMS_STATS.GATHER_TABLE_STATS(USER, 'REQUESTS');
--   EXEC DBMS_STATS.GATHER_TABLE_STATS(USER, 'ACTIVITY_LOG');
