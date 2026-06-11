package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.RequestRepository;

@Service
@RequiredArgsConstructor
public class DeleteRequestUseCase {

    private final RequestRepository requestRepository;

    @Transactional
    public void execute(GetSingleRequestQuery query) {
        Request request = requestRepository.findByExternalId(query.externalId())
                .orElseThrow(() -> RequestNotFoundException.withExternalId(query.externalId()));

        if (!query.isAdminOrDean() && !request.getUser().getEmail().equals(query.userEmail())) {
            throw UnauthorizedRequestAccessException.forAction("delete");
        }

        // Students can only delete requests in DRAFT state. Admins/Deans can delete anything.
        if (!query.isAdminOrDean() && !request.getStatus().getName().equals("DRAFT")) {
            throw InvalidRequestStateException.notInDraft();
        }

        requestRepository.delete(request);
    }
}
