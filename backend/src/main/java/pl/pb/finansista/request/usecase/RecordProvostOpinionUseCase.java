package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RecordProvostOpinionUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessSpecificationFactory accessSpecFactory;

    @Transactional
    public void execute(RecordProvostOpinionCommand command) {
        User actor = userRepository.findByExternalId(command.userExternalId())
                .orElseThrow(UserNotFoundException::new);

        Specification<Request> spec = Specification.allOf(
                RequestSpecifications.hasExternalId(command.requestExternalId()),
                accessSpecFactory.createForUser(actor, command.userAuthorities())
        );

        Request request = requestRepository.findOne(spec)
                .orElseThrow(RequestNotFoundException::new);

        boolean isAdmin = command.userAuthorities().contains(RoleName.ROLE_ADMIN.name());
        boolean isProvost = command.userAuthorities().contains(RoleName.ROLE_PROVOST.name());
        if (!isAdmin && !isProvost) {
            throw UnauthorizedRequestAccessException.forRecordingProvostOpinion();
        }

        if (!request.getStatus().getName().equals(RequestStatusName.FORMAL_EVALUATION.name())) {
            throw InvalidRequestStateException.withStatusName(request.getStatus().getName());
        }

        request.recordProvostOpinion(command.opinion());
        requestRepository.save(request);

        if (!request.getStatus().getName().equals(RequestStatusName.FORMAL_EVALUATION.name())) {
          throw InvalidRequestStateException.withStatusName(request.getStatus().getName());
        }

        request.recordProvostOpinion(command.opinion());
        requestRepository.save(request);
  }
}
