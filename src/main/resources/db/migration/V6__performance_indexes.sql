-- Indeksy pod najczęstsze filtry/joiny na tabeli transakcyjnej (requests):
CREATE INDEX idx_requests_dept ON requests(id_d);
CREATE INDEX idx_requests_status ON requests(id_rs);
CREATE INDEX idx_requests_user ON requests(id_u);
CREATE INDEX idx_requests_cost_cat ON requests(id_cc);
CREATE INDEX idx_requests_created_at ON requests(created_at);

-- Indeks złożony pod listy filtrowane po wydziale i sortowane po dacie
CREATE INDEX idx_requests_dept_created ON requests(id_d, created_at);

-- Tabele powiązane pobierane zawsze per wniosek:
CREATE INDEX idx_activity_request ON activity_log(id_req);
CREATE INDEX idx_activity_user ON activity_log(id_u);
CREATE INDEX idx_comments_request ON comments(id_req);
CREATE INDEX idx_attachments_request ON attachments(id_req);

-- Ograniczenie wartości w tabeli request: kwota wniosku musi być dodatnia
ALTER TABLE requests ADD CONSTRAINT chk_requests_amount_positive CHECK (amount > 0);

-- Wskazówka do testów wydajnościowych (uruchom po załadowaniu danych demo):
--   EXEC DBMS_STATS.GATHER_TABLE_STATS(USER, 'REQUESTS');
--   EXEC DBMS_STATS.GATHER_TABLE_STATS(USER, 'ACTIVITY_LOG');
