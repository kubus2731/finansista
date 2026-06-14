-- V10: Sekcja VI zgodnie z PDF - wniosek może mieć WIELE źródeł finansowania.
-- Pojedyncze requests.funding_source_id zostaje zastąpione wierszami request_funding
-- powiązanymi ze słownikiem funding_source. Dodano "podpis dysponenta"
-- (kto i kiedy przyznał środki) odpowiadający kolumnie podpisu w tabeli VI.

-- 1. Usunięcie pojedynczego źródła z wniosku (zastąpione wierszami request_funding).
ALTER TABLE requests DROP CONSTRAINT fk_request_funding_source;
ALTER TABLE requests DROP COLUMN funding_source_id;

-- 2. request_funding: powiązanie ze słownikiem zamiast wolnego tekstu + podpis dysponenta.
ALTER TABLE request_funding DROP COLUMN source_name;
ALTER TABLE request_funding ADD funding_source_id NUMBER(15);
ALTER TABLE request_funding ADD granted_by_id NUMBER(15);
ALTER TABLE request_funding ADD granted_at TIMESTAMP;

ALTER TABLE request_funding MODIFY (amount_requested NUMBER(15,2) NOT NULL);
ALTER TABLE request_funding MODIFY (funding_source_id NUMBER(15) NOT NULL);

ALTER TABLE request_funding ADD CONSTRAINT fk_reqfunding_source
    FOREIGN KEY (funding_source_id) REFERENCES funding_source(id);
ALTER TABLE request_funding ADD CONSTRAINT fk_reqfunding_granted_by
    FOREIGN KEY (granted_by_id) REFERENCES users(id);
ALTER TABLE request_funding ADD CONSTRAINT uq_reqfunding_request_source
    UNIQUE (request_id, funding_source_id);

-- 3. Nowa rola: Centrum Spraw Studenckich (CSSDiR) - ocena formalna + opinia prorektora.
INSERT INTO roles (id, name) VALUES (8, 'ROLE_STUDENT_AFFAIRS');

COMMIT;
