package pl.pb.finansista.request.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GetAvailableTransitionsUseCase {

  private final RequestRepository requestRepository;
  private final UserRepository userRepository;
  private final RequestAccessSpecificationFactory accessSpecFactory;
  private final RequestTransitionValidator transitionValidator;

  @Transactional(readOnly = true)
  public List<String> execute(GetSingleRequestQuery query) {
    User actor =
        userRepository
            .findByExternalId(query.userExternalId())
            .orElseThrow(UserNotFoundException::new);

    Specification<Request> spec =
        Specification.allOf(
            RequestSpecifications.hasExternalId(query.externalId()),
            accessSpecFactory.createForUser(actor, query.userAuthorities()));

    Request request = requestRepository.findOne(spec).orElseThrow(RequestNotFoundException::new);

    return transitionValidator
        .getAvailableTransitions(request, actor, query.userAuthorities())
        .stream()
        .map(Enum::name)
        .toList();
  }
}
