package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.RequestRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSingleRequestUseCase {

    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public Request execute(GetSingleRequestQuery query) {
        Request request = requestRepository.findByExternalId(query.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!query.isAdminOrDean() && !request.getUser().getEmail().equals(query.userEmail())) {
            throw new UnauthorizedRequestAccessException("You do not have permission to view this request");
        }

        return request;
    }
}
