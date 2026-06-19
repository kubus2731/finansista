package pl.pb.finansista.request.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.repository.RequestStatusRepository;

@Service
@RequiredArgsConstructor
public class GetAllRequestStatusesUseCase {

    private final RequestStatusRepository requestStatusRepository;

    @Transactional(readOnly = true)
    public List<RequestStatus> execute() {
        return requestStatusRepository.findAll();
    }
}
