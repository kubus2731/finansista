package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.exception.RequestTemplateNotFoundException;
import pl.pb.finansista.request.repository.RequestTemplateRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSingleRequestTemplateUseCase {

    private final RequestTemplateRepository requestTemplateRepository;

    @Transactional(readOnly = true)
    public RequestTemplate execute(UUID id) {
        return requestTemplateRepository.findByExternalId(id)
                .orElseThrow(RequestTemplateNotFoundException::new);
    }
}
