package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.FundingSourceName;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestFunding;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.InvalidRequestStateException;
import pl.pb.finansista.request.exception.RequestFundingNotFoundException;
import pl.pb.finansista.request.exception.RequestNotFoundException;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.request.repository.RequestRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GrantFundingUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestAccessSpecificationFactory accessSpecFactory;
    private final RequestFundingAuthorization fundingAuthorization;

    @Transactional
    public void execute(GrantFundingCommand command) {
        User actor = userRepository.findByExternalId(command.userExternalId())
                .orElseThrow(UserNotFoundException::new);

        Specification<Request> spec = Specification.allOf(
                RequestSpecifications.hasExternalId(command.requestExternalId()),
                accessSpecFactory.createForUser(actor, command.userAuthorities())
        );

        Request request = requestRepository.findOne(spec)
                .orElseThrow(RequestNotFoundException::new);

        if (!request.getStatus().getName().equals(RequestStatusName.UNDER_REVIEW.name())) {
            throw InvalidRequestStateException.withStatusName(request.getStatus().getName());
        }

        RequestFunding row = request.fundingFor(command.fundingSourceId())
                .orElseThrow(RequestFundingNotFoundException::new);

        boolean isSameDepartment = request.getDepartment().getId().equals(actor.getDepartment().getId());
        if (!fundingAuthorization.canGrantSource(command.userAuthorities(),
                FundingSourceName.valueOf(row.getSource().getName()), isSameDepartment)) {
            throw UnauthorizedRequestAccessException.forAction("grant funding from " + row.getSource().getName());
        }

        row.grant(command.amountGranted(), actor);
        requestRepository.save(request);
    }
}
