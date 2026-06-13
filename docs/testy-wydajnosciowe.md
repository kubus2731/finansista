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
