package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAvailableTransitionsUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestTransitionValidator transitionValidator;

    @Transactional(readOnly = true)
    public List<String> execute(GetSingleRequestQuery query) {
        Request request = requestRepository.findByExternalId(query.externalId())
                .orElseThrow(RequestNotFoundException::new);

        User actor = userRepository.findByEmail(query.userEmail())
                .orElseThrow(UserNotFoundException::new);

        return transitionValidator.getAvailableTransitions(request, actor, query.userAuthorities()).stream()
                .map(Enum::name)
                .toList();
    }
}
