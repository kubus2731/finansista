# Testy wydajnościowe bazy danych (SBD pkt 6)

## Cel i metodologia

Testom poddano tabelę transakcyjną **`REQUESTS`** (najczęściej aktualizowaną w systemie),
zgodnie z wymaganiami: pomiary dla **1 000 / 10 000 / 100 000 / 1 000 000** rekordów,
weryfikacja głównych zapytań aplikacji wraz z mechanizmem paginacji oraz ocena
zaproponowanych indeksów.

- Środowisko: Oracle Database 23ai Free (kontener Docker `gvenzl/oracle-free`), schemat `ADMIN_PB`.
- Dane testowe: generator set-based (`db/scripts/demo_data_load.sql`) — wstawianie
  przyrostowe `INSERT ... SELECT` z `CONNECT BY`, losowe kwoty (`DBMS_RANDOM`),
  równomierny rozkład po użytkownikach/statusach/wydziałach/kategoriach (`MOD`).
- Po każdym zasileniu odświeżano statystyki optymalizatora
  (`DBMS_STATS.GATHER_TABLE_STATS`).
- Pomiar: `SET TIMING ON` w SQL*Plus; plany wykonania: `EXPLAIN PLAN` + `DBMS_XPLAN`.
- Skrypt pomiarowy: `db/scripts/performance_test.sql`.

## Testowane zapytania

| Nr | Zapytanie | Scenariusz w aplikacji |
|----|-----------|------------------------|
| Q1 | `ORDER BY created_at DESC OFFSET 40 FETCH NEXT 20` | paginacja listy wniosków (3. strona) |
| Q2 | `WHERE department_id = 1 ORDER BY created_at DESC FETCH 20` | przegląd wniosków wydziału |
| Q3 | `SELECT * FROM v_department_requests_summary` | raport admina: ilości i koszty per wydział |
| Q4 | `WHERE external_id = :uuid` | pobranie wniosku po identyfikatorze z API/linku |

## Wyniki (czas wykonania)

| Zapytanie | 1 tys. | 10 tys. | 100 tys. | 1 mln |
|-----------|-------:|--------:|---------:|------:|
| Q1 — paginacja listy        | 0,01 s | 0,01 s | 0,04 s | 0,27 s |
| Q2 — filtr wydziału + sort  | 0,01 s | 0,00 s | 0,03 s | 0,17 s |
| Q3 — raport agregowany      | 0,01 s | 0,01 s | 0,03 s | 0,31 s |
| Q4 — wyszukanie po UUID     | 0,00 s | 0,00 s | 0,00 s | 0,00 s |

## Plany wykonania (skala 1 mln)

**Q2 — filtr wydziału z sortowaniem** wykorzystuje indeks złożony
`IDX_REQUESTS_DEPT_CREATED (department_id, created_at)`; sortowanie odbywa się
bez operacji SORT (odczyt indeksu od końca), a silnik przerywa po pobraniu 20 wierszy:

```
| WINDOW NOSORT STOPKEY         |                            |  20 | 12 (0) |
|  TABLE ACCESS BY INDEX ROWID  | REQUESTS                   |     |        |
|   INDEX RANGE SCAN DESCENDING | IDX_REQUESTS_DEPT_CREATED  |  20 |  3 (0) |
```

**Q4 — wyszukanie po `external_id`** korzysta z unikalnego indeksu (constraint UNIQUE):

```
| TABLE ACCESS BY INDEX ROWID | REQUESTS    | 1 | 3 (0) |
|  INDEX UNIQUE SCAN          | SYS_C008705 | 1 | 2 (0) |
```

## Wnioski

1. **Zaproponowane indeksy (migracja V6) spełniają zadanie.** Kluczowe zapytania
   ekranowe (Q2, Q4) działają w czasie praktycznie stałym niezależnie od skali —
   koszt planu dla Q2 przy 1 mln wierszy to 12 jednostek (20 wierszy z indeksu),
   a wyszukanie po UUID to pojedynczy `INDEX UNIQUE SCAN`.
2. **Paginacja `OFFSET/FETCH` (Q1) skaluje się akceptowalnie** (0,27 s przy 1 mln),
   ale jej koszt rośnie z głębokością strony (baza musi policzyć pominięte wiersze).
   Przy bardzo głębokim przewijaniu rekomendowana technika **keyset pagination**
   (`WHERE created_at < :ostatnia_data ... FETCH NEXT 20`), korzystająca wprost
   z `IDX_REQUESTS_CREATED_AT`.
3. **Raport agregowany (Q3) czyta pełną tabelę** — 0,31 s przy 1 mln wierszy jest
   w pełni akceptowalne dla raportu administracyjnego. Gdyby tabela istotnie urosła,
   naturalnym krokiem jest widok zmaterializowany odświeżany cyklicznie.
4. Wstawienie 900 tys. wierszy metodą set-based trwało pojedyncze sekundy, co
   potwierdza przepustowość zapisu przy ładowaniu wsadowym.

## Odtworzenie testów

```bash
# 1. Załaduj dane do wybranej skali (przyrostowo)
sqlplus admin_pb/...@//localhost:1521/FREEPDB1 @db/scripts/demo_data_load.sql 1000000

# 2. Uruchom pomiary
sqlplus admin_pb/...@//localhost:1521/FREEPDB1 @db/scripts/performance_test.sql

# 3. Sprzątanie danych testowych
#    DELETE FROM requests WHERE title LIKE 'Wniosek testowy #%'; COMMIT;
```

---

# Testy wydajnościowe endpointów REST (bonus SBD)

## Cel i metodologia

Uzupełnieniem testów na poziomie SQL są testy całych ścieżek HTTP — sprawdzają,
jak warstwa aplikacji (Spring Boot + JPA + serializacja JSON) zachowuje się
pod realnym, równoległym obciążeniem klientów. Mierzony jest endpoint
**`GET /api/v1/requests`**, który zwraca paginowaną listę wniosków
z eager-fetchem pięciu asocjacji (`status`, `department`, `costCategory`,
`fundingSource`, `template`).

- Narzędzie: **k6 v2.0.0** (Grafana Labs), skrypt `docs/k6/get-requests.k6.js`.
- Profil obciążenia: ramp-up 0 → 100 → 500 → **1000 VU**, plateau 2 min na szczycie,
  ramp-down 30 s. Łączny czas testu: 4 min.
- Sygnalizacja błędów: progi (`thresholds`) `p95 < 800 ms`, `errors < 1%` —
  przekroczenie generuje exit code ≠ 0 i wpis `ERRO` w stdout, co pozwala
  obiektywnie udokumentować wąskie gardło.
- Środowisko: backend i baza Oracle 23ai uruchomione lokalnie na tym samym
  hoście (Windows 11, 16 GB RAM); brak osobnej maszyny generującej obciążenie,
  co ma niewielki wpływ na bezwzględne wartości latencji, ale nie wpływa na
  porównanie wariantów.

Test powtórzono dla czterech wartości parametru zapytania **`size`**
(liczba elementów na stronie), żeby zweryfikować skuteczność paginacji
i odnaleźć wąskie gardło.

## Wyniki — 1000 VU, 4 minuty, GET /api/v1/requests

| `size` | Łącznie żądań | Avg | p95 | p99 | HTTP fail | Checks fail (>1500 ms) |
|--------|---------------|-----|-----|-----|-----------|------------------------|
| 10     | 74 700        | 675 ms | 1481 ms | 1624 ms | 0% | 4% |
| 50     | 75 145        | 665 ms | 1477 ms | 1628 ms | 0% | 4% |
| 500    | 71 852        | 742 ms | 1599 ms | 1727 ms | 0% | 9% |
| 1000   | 72 438        | 730 ms | 1777 ms | n/a*    | 0% | 9% |

\* p99 nie zostało wystawione w pierwszym przebiegu — przed kolejnymi
testami dodano `summaryTrendStats` do konfiguracji k6.

## Analiza wąskich gardeł

1. **HTTP layer jest stabilny** — `http_req_failed = 0%` we wszystkich
   przebiegach. Backend nie zwrócił ani jednego 5xx, nie było timeoutów
   ani zerwanych połączeń. Latencja jest problemem, dostępność — nie.

2. **Rozmiar strony 10 vs 50 daje niemal identyczne wyniki**
   (avg 675 vs 665 ms, p95 1481 vs 1477 ms). Oznacza to, że **dominującym
   kosztem nie jest serializacja JSON ani rozmiar payloadu**, tylko coś
   stałego per-request. Najpewniejsze podejrzenia:
   - **pula HikariCP (default 10)** — przy 1000 współbieżnych VU jest
     to gardło numer jeden; większość czasu request spędza w kolejce na
     połączenie do bazy,
   - narzut Spring Security + filtra JWT (parsowanie tokena na każdy request),
   - serializacja `RequestResponse` z zagnieżdżonymi listami (Task / CostItem
     / Funding) — koszt fixed nawet dla małej strony.

3. **Skok przy `size=500` i `size=1000`** (errors 4% → 9%, p95 +20%).
   Dopiero duże strony zaczynają obciążać CPU serializacją i pamięć (większy
   working set per request). To zgadza się z teorią — **rekomendowana
   domyślna strona dla tego endpointu to `size ≤ 50`**.

4. **Próg `p95 < 800 ms` jest nieosiągalny przy 1000 VU na tym sprzęcie**
   niezależnie od rozmiaru strony. Realny SLO dla aplikacji uniwersyteckiej
   (kilkaset jednoczesnych użytkowników) — `p95 < 500 ms` przy
   `size ≤ 50` — wymagałby:
   - zwiększenia `spring.datasource.hikari.maximum-pool-size` do ~50,
   - cache'owania uprawnień JWT na request (uniknięcie powtórnego parsowania),
   - rozważenia **keyset pagination** dla głębokich stron (analogicznie do
     rekomendacji z testów SQL, sekcja Q1).

5. **Throughput stabilizuje się na ~300 RPS** niezależnie od `size`
   (71–75 tys. żądań / 4 min = ~300/s). To bezpośrednio potwierdza tezę
   z punktu 2: gardłem jest dostępność wątku/connection, nie objętość
   pojedynczej odpowiedzi.

## Odtworzenie testów

```powershell
# 1. Uruchom backend (osobny terminal)
.\mvnw spring-boot:run

# 2. Zaloguj się REST-em i przechwyć JWT z cookie
$resp = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/login" `
        -Method POST -ContentType "application/json" `
        -Body '{"email":"j.matusiewicz@student.pb.edu.pl","password":"admin123"}' `
        -SessionVariable s
$env:K6_JWT = ($s.Cookies.GetCookies("http://localhost:8080") | ? Name -eq "jwt").Value

# 3. Cztery przebiegi (po ~4 min każdy)
k6 run --env PAGE_SIZE=10   docs/k6/get-requests.k6.js
k6 run --env PAGE_SIZE=50   docs/k6/get-requests.k6.js
k6 run --env PAGE_SIZE=500  docs/k6/get-requests.k6.js
k6 run --env PAGE_SIZE=1000 docs/k6/get-requests.k6.js
```

Pełny raport JSON z ostatniego przebiegu trafia do
`docs/k6/get-requests.summary.json` (umożliwia dalszą analizę
i porównania w czasie).

