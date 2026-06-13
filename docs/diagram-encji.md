# Diagram encji (ERD) — Finansista PB

Schemat bazy danych po migracjach V1–V8 (Oracle).
Renderuje się na GitHubie oraz w podglądzie Mermaid (IntelliJ / VS Code).

```mermaid
erDiagram
    ROLES ||--o{ USERS : "ma role"
    DEPARTMENT ||--o{ USERS : "przynalezy"
    DEPARTMENT ||--o{ REQUESTS : "dotyczy"
    USERS ||--o{ REQUESTS : "sklada"
    REQUEST_STATUS ||--o{ REQUESTS : "status"
    REQUEST_TEMPLATES ||--o{ REQUESTS : "wzor"
    COST_CATEGORY ||--o{ REQUESTS : "kategoria"
    FUNDING_SOURCE ||--o{ REQUESTS : "zrodlo"

    REQUESTS ||--o{ COMMENTS : "komentarze"
    USERS ||--o{ COMMENTS : "autor"
    REQUESTS ||--o{ ATTACHMENTS : "zalaczniki"
    REQUESTS ||--o{ ACTIVITY_LOG : "historia"
    USERS ||--o{ ACTIVITY_LOG : "wykonal"
    REQUEST_STATUS ||--o{ ACTIVITY_LOG : "zmiana statusu"

    REQUESTS ||--o{ REQUEST_TASK : "harmonogram"
    REQUESTS ||--o{ REQUEST_COST_ITEM : "kosztorys"
    REQUESTS ||--o{ REQUEST_FUNDING : "zrodla finansowania"

    ROLES {
        number id PK
        string name "np. ROLE_STUDENT, ROLE_ADMIN"
    }
    DEPARTMENT {
        number id PK
        string name
    }
    USERS {
        number id PK
        raw external_id UK
        string name
        string surname
        string email UK
        string phone_number UK
        string password
        number role_id FK
        number department_id FK
    }
    REQUEST_STATUS {
        number id PK
        string name "DRAFT..ACCEPTED"
    }
    REQUEST_TEMPLATES {
        number id PK
        string title
        clob description
        boolean active
    }
    COST_CATEGORY {
        number id PK
        string name
    }
    FUNDING_SOURCE {
        number id PK
        string name
    }
    REQUESTS {
        number id PK
        raw external_id UK
        string title
        clob description
        number amount
        number user_id FK
        number request_status_id FK
        number request_template_id FK
        number department_id FK
        number cost_category_id FK
        number funding_source_id FK
        string realizer_type "V8: sekcja I"
        string project_kind "V8"
        string project_scope "V8"
        string project_nature "V8"
        date planned_date_from "V8"
        date planned_date_to "V8"
        string location "V8"
        number participants_involved "V8"
        string supervisor_name "V8: sekcja II"
        clob provost_opinion "V8: sekcja III"
    }
    COMMENTS {
        number id PK
        raw external_id UK
        number request_id FK
        number user_id FK
        clob content
    }
    ATTACHMENTS {
        number id PK
        raw external_id UK
        number request_id FK
        string file_name
        string file_url
    }
    ACTIVITY_LOG {
        number id PK
        number request_id FK
        number user_id FK
        number old_status_id FK
        number new_status_id FK
        string description
    }
    REQUEST_TASK {
        number id PK
        number request_id FK
        number task_no
        string name
        date date_from
        date date_to
        number planned_cost
        clob actions
    }
    REQUEST_COST_ITEM {
        number id PK
        number request_id FK
        number task_no
        string item_name
        number quantity
        number unit_cost
        string notes
    }
    REQUEST_FUNDING {
        number id PK
        number request_id FK
        string source_name
        number amount_requested
        number amount_granted
    }
```

## Legenda

- `PK` — klucz główny, `FK` — klucz obcy, `UK` — unikalny.
- `||--o{` — relacja jeden-do-wielu (jeden rekord po lewej, wiele po prawej).
- Hybrydowy klucz: wewnętrzny `id` (NUMBER IDENTITY) + zewnętrzny `external_id` (RAW(16)/UUID) wystawiany na zewnątrz w API i adresach URL.
- Kolumny oznaczone `V8` dodano przy odwzorowaniu Załącznika nr 1 (sekcje I–III); tabele `REQUEST_TASK`, `REQUEST_COST_ITEM`, `REQUEST_FUNDING` to sekcje IV i VI.
- Tabele słownikowe (ROLES, DEPARTMENT, REQUEST_STATUS, COST_CATEGORY, FUNDING_SOURCE, REQUEST_TEMPLATES) wypełnia migracja V2.
