package pl.pb.finansista.request.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pl.pb.finansista.request.view.CreateRequestForm;
import pl.pb.finansista.request.view.RequestView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile("mock")
public class MockRequestService implements RequestService {

    private final List<RequestView> requests = new ArrayList<>(List.of(
            new RequestView(
                    UUID.randomUUID(),
                    "Dofinansowanie wyjazdu na konferencję koła naukowego",
                    new BigDecimal("4200.00"),
                    "SUBMITTED",
                    "Wydział Informatyki PB",
                    "Jan Wnioskodawca",
                    LocalDate.of(2026, 5, 18)),
            new RequestView(
                    UUID.randomUUID(),
                    "Organizacja juwenaliów wydziałowych",
                    new BigDecimal("12500.00"),
                    "UNDER_REVIEW",
                    "Samorząd Studentów PB",
                    "Anna Nowak",
                    LocalDate.of(2026, 5, 22)),
            new RequestView(
                    UUID.randomUUID(),
                    "Zakup podzespołów do projektu inżynierskiego",
                    new BigDecimal("1850.50"),
                    "ACCEPTED",
                    "Wydział Mechaniczny PB",
                    "Piotr Kowalczyk",
                    LocalDate.of(2026, 4, 30)),
            new RequestView(
                    UUID.randomUUID(),
                    "Wniosek o nagrody w konkursie programistycznym",
                    new BigDecimal("3000.00"),
                    "DRAFT",
                    "Wydział Informatyki PB",
                    "Jan Wnioskodawca",
                    LocalDate.of(2026, 6, 1))
    ));

    @Override
    public List<RequestView> findAll() {
        return requests;
    }

    @Override
    public Optional<RequestView> findById(UUID id) {
        return requests.stream()
                .filter(request -> request.id().equals(id))
                .findFirst();
    }

    @Override
    public RequestView create(CreateRequestForm form) {
        RequestView created = new RequestView(
                UUID.randomUUID(),
                form.title(),
                form.amount(),
                "DRAFT",
                "Wydział Informatyki PB",
                "Jan Wnioskodawca",
                LocalDate.now()
        );
        requests.add(created);
        return created;
    }
}
