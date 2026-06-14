package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.RequestStatus;
import pl.pb.finansista.request.repository.RequestStatusRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllRequestStatusesUseCase {

    private final RequestStatusRepository requestStatusRepository;

    @Transactional(readOnly = true)
    public List<RequestStatus> execute() {
        return requestStatusRepository.findAll();
    }
}
