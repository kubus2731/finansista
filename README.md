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

### 1) Baza danych

```powershell
# z katalogu głównego projektu
docker compose up -d
```

To podnosi kontener `pb_finansista_db` (Oracle 23ai Free) z:
- portem `1521` zmapowanym na host,
- userem `admin_pb` / hasłem `paw1ewjav1e`,
- service name `FREEPDB1`.

Pierwsze uruchomienie pobiera obraz (~2 GB) i inicjalizuje bazę
— może trwać kilka minut. Sprawdź czy gotowe:

```powershell
docker logs pb_finansista_db --tail 20
# szukaj linijki "DATABASE IS READY TO USE!"
```

### 2) Build obu modułów (jednorazowo)

```powershell
./mvnw clean install -DskipTests
```

To buduje `backend` i `frontend` jako uruchamialne JAR-y oraz instaluje
`finansista-backend` do lokalnego repozytorium Maven (frontend ma go jako
tymczasową zależność do współdzielenia DTO).

### 3) Backend (terminal #1)

```powershell
cd backend
../mvnw spring-boot:run
```

Przy starcie:
- Flyway wykonuje migracje V1–V8 (schemat, słowniki, widoki, triggery,
  pakiet, indeksy, maszyna stanów, dane demo),
- `DataSeeder` dosiewa 6 kont demo z hasłami BCrypt
  (idempotentnie — sprawdza `existsByEmail`),
- aplikacja nasłuchuje na **`http://localhost:8080`**.

### 4) Frontend (terminal #2)

```powershell
cd frontend
../mvnw spring-boot:run
```

UI nasłuchuje na **`http://localhost:8081`**.

### 5) Sprawdzenie że żyje

Otwórz w przeglądarce:

| URL | Co tam jest |
|---|---|
| http://localhost:8081 | strona główna (frontend) |
| http://localhost:8081/login | logowanie |
| http://localhost:8080/swagger-ui.html | dokumentacja REST API (Swagger) |
| http://localhost:8080/v3/api-docs | OpenAPI spec w JSON |

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

```powershell
# zatrzymanie aplikacji: Ctrl+C w każdym terminalu

# zatrzymanie bazy (zachowuje dane):
docker compose stop

# pełny reset bazy (USUWA dane i wolumen):
docker compose down -v
```
