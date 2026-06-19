# Finansista PB

![Java](https://img.shields.io/badge/Java-25-orange?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-brightgreen?logo=springboot&logoColor=white)
![Oracle DB](https://img.shields.io/badge/Oracle-23ai-red?logo=oracle&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-UI-blue)

**Finansista PB** to nowoczesna aplikacja webowa stworzona na potrzeby Politechniki Białostockiej, służąca do kompleksowej obsługi, procedowania i zarządzania wnioskami finansowymi. System cyfryzuje i upraszcza obieg dokumentów pomiędzy studentami, samorządem (WRSS), komisjami, dziekanatami a kwesturą.

## Architektura

Multi-module Maven:

```
finansista-parent (pom)
├── backend/      → REST API   (Spring Boot, port 8080)
└── frontend/     → UI Thymeleaf (Spring Boot, port 8081)
```

Frontend komunikuje się z backendem **wyłącznie przez REST** (`RestClient`),
przekazując token JWT z ciasteczka jako `Authorization: Bearer`.

## Wymagania środowiska

- Java 25
- Maven 3.9+ (wbudowany wrapper `mvnw` wystarczy)
- Docker Desktop

## Uruchomienie — krok po kroku

### Wariant A: całość w Dockerze (zalecane)

Jedna komenda buduje obrazy i podnosi wszystkie trzy serwisy
(baza + backend + frontend):

```bash
# z katalogu głównego projektu
docker compose up -d --build
```

Podnosi to:
- `pb_finansista_db` — Oracle Free (port `1521`, user `admin_pb`,
  service name `FREEPDB1`),
- `pb_finansista_backend` — REST API na **`http://localhost:8080`**,
- `pb_finansista_frontend` — UI na **`http://localhost:8081`**.

Kolejność startu jest pilnowana healthcheckami: backend rusza dopiero gdy
baza jest zdrowa, a frontend gdy zdrowy jest backend. Pierwsze uruchomienie
pobiera obraz Oracle (~2 GB) i buduje obrazy aplikacji — może potrwać kilka
minut. Status:

```bash
docker compose ps
# czekaj aż "oracle-db" i "backend" będą (healthy)
```

Przy starcie backendu Flyway wykonuje migracje V1–V10 (schemat, słowniki,
widoki, triggery, pakiet, indeksy, maszyna stanów, dane demo), a `DataSeeder`
idempotentnie dosiewa konta demo.

#### Konfiguracja (`.env`)

Domyślne wartości są wbudowane w `docker-compose.yml`, więc `docker compose up`
działa bez żadnej konfiguracji. Aby je nadpisać, skopiuj szablon i zmień co
trzeba:

```bash
cp .env.example .env
```

Najważniejsze zmienne: porty (`BACKEND_PORT`, `FRONTEND_PORT`, `DB_PORT`),
sekrety bazy, oraz konfiguracja JWT i dokumentacji API:

| Zmienna | Znaczenie |
|---|---|
| `JWT_SECRET` | klucz HMAC podpisujący token (zmień w prod) |
| `JWT_EXPIRATION` | czas życia tokenu i ciasteczka w ms |
| `JWT_COOKIE_SECURE` | `true` za HTTPS |
| `SPRINGDOC_API_DOCS_ENABLED` / `SPRINGDOC_SWAGGER_UI_ENABLED` | włącz/wyłącz Swagger / OpenAPI |
| `MANAGEMENT_PORT` | wewnętrzny port actuatora (nie publikowany na host) |

### Wariant B: dev lokalny (moduły z Maven)

Aby uruchamiać aplikacje spoza kontenerów (np. szybsza iteracja), podnieś
samą bazę, a moduły odpal przez wrapper:

```bash
docker compose up -d oracle-db
./mvnw clean install -DskipTests
cd backend  && ../mvnw spring-boot:run   # terminal #1 → :8080
cd frontend && ../mvnw spring-boot:run   # terminal #2 → :8081
```

> **Uwaga:** backend nie ma już wbudowanych wartości domyślnych JWT, więc do
> lokalnego startu musi dostać zmienne z `.env` (sekret, czas życia, nazwa i
> tryb ciasteczka). Najprościej załadować je do powłoki przed startem:
>
> ```bash
> set -a; source .env; set +a
> ```

### Sprawdzenie że żyje

| URL | Co tam jest |
|---|---|
| http://localhost:8081 | strona główna (frontend) |
| http://localhost:8081/login | logowanie |
| http://localhost:8080/swagger-ui.html | dokumentacja REST API (Swagger, jeśli włączony) |
| http://localhost:8080/v3/api-docs | OpenAPI spec w JSON (jeśli włączony) |

## Konta testowe

### Z `DataSeeder` (BCrypt, hashowane w runtime)

| E-mail | Hasło | Rola |
|---|---|---|
| j.matusiewicz@student.pb.edu.pl | admin123 | ADMIN |
| j.borkowski@student.pb.edu.pl   | admin123 | ADMIN |
| j.wnioskodawca@student.pb.edu.pl | student123 | STUDENT |
| k.samorzad@pb.edu.pl | wrss123 | WRSS |
| a.zgodna@pb.edu.pl | komisja123 | LEGAL_COMMISSION |
| e.dziekan@pb.edu.pl | dziekanat123 | DEAN_OFFICE |

### Z migracji `V8__demo_data.sql` (`{noop}`, plain-text dla demo)

| E-mail | Hasło | Rola |
|---|---|---|
| demo-admin@pb.edu.pl | demo123 | ADMIN |
| demo-student@pb.edu.pl | demo123 | STUDENT |
| demo-wrss@pb.edu.pl | demo123 | STUDENT_COUNCIL |
| demo-komisja@pb.edu.pl | demo123 | LEGAL_COMMISSION |
| demo-dziekanat@pb.edu.pl | demo123 | DEAN_OFFICE |
| demo-kwestura@pb.edu.pl | demo123 | FINANCE_OFFICE |

Razem z kontami demo migracja V8 wstawia 3 przykładowe wnioski
(DRAFT / SUBMITTED / ACCEPTED) wraz z rekordami `request_funding`.

## Testy wydajnościowe

- **Bazodanowe (SQL):** skrypty w
  `backend/src/main/resources/db/scripts/` —
  `demo_data_load.sql N` (przyrostowo dociąża do N wierszy)
  oraz `performance_test.sql` (Q1–Q4 + plany wykonania).
  Wyniki i analiza: `docs/testy-wydajnosciowe.md`.
- **Endpointów REST (k6):** `docs/k6/get-requests.k6.js`
  (1000 VU, 4 min, profil ramp-up). Wymaga zainstalowanego
  [k6](https://k6.io). Wyniki i wnioski:
  `docs/testy-wydajnosciowe.md` oraz `docs/dokument sbd/dokumentacja.pdf`
  (sekcja *Testy endpointów REST*).

## Dokumentacja projektowa

- `docs/dokument sbd/dokumentacja.pdf` — pełna dokumentacja
  (architektura, model danych, PL/SQL, REST API, bezpieczeństwo,
  testy wydajnościowe, uruchomienie).
- `docs/diagram-encji.md` + `docs/dokument sbd/diagram_encji.png` — ERD.
- `docs/dokument sbd/maszyna_stanow.png` — diagram statusów wniosku.

## Sprzątanie

```bash
# zatrzymanie wszystkich serwisów (zachowuje dane):
docker compose stop

# zatrzymanie + usunięcie kontenerów (zachowuje wolumeny):
docker compose down

# pełny reset (USUWA dane bazy i załączniki):
docker compose down -v
```
