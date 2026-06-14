package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.repository.RequestTemplateRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetActiveRequestTemplatesUseCase {

    private final RequestTemplateRepository requestTemplateRepository;

    @Transactional(readOnly = true)
    public List<RequestTemplate> execute() {
        return requestTemplateRepository.findActiveTemplates();
    }
}
