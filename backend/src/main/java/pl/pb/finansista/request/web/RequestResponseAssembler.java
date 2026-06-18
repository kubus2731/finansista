package pl.pb.finansista.request.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pb.finansista.reference.FundingSourceName;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestFunding;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.usecase.RequestFundingAuthorization;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Builds {@link RequestResponse} together with the per-actor authorization flags.
 * <p>
 * The flags mirror exactly the rules enforced by the mutating use cases
 * (Edit/Delete/AddAttachment/RecordProvostOpinion/GrantFunding) so the UI only ever
 * offers an action the backend would actually allow. Authorization stays on the backend —
 * the frontend just renders what it receives.
 */
@Component
@RequiredArgsConstructor
public class RequestResponseAssembler {

    private final RequestFundingAuthorization fundingAuthorization;
    private final UserRepository userRepository;

    public RequestResponse toResponse(Request request, UUID actorExternalId, Collection<String> roles) {
        User actor = userRepository.findByExternalId(actorExternalId).orElse(null);
        return build(request, actor, actorExternalId, roles);
    }

    public List<RequestResponse> toResponses(List<Request> requests, UUID actorExternalId, Collection<String> roles) {
        User actor = userRepository.findByExternalId(actorExternalId).orElse(null);
        return requests.stream()
                .map(r -> build(r, actor, actorExternalId, roles))
                .toList();
    }

    private RequestResponse build(Request request, User actor, UUID actorExternalId, Collection<String> roles) {
        String status = request.getStatus().getName();
        boolean isAdmin = roles.contains(RoleName.ROLE_ADMIN.name());
        boolean isAuthor = request.getUser().getExternalId().equals(actorExternalId);
        boolean isStudentAffairs = roles.contains(RoleName.ROLE_STUDENT_AFFAIRS.name());

        boolean editable = status.equals(RequestStatusName.DRAFT.name())
                || status.equals(RequestStatusName.CORRECTION_REQUIRED.name());

        boolean canEdit = (isAdmin || isAuthor) && editable;
        boolean canDelete = isAdmin || (isAuthor && status.equals(RequestStatusName.DRAFT.name()));
        boolean canManageAttachments = (isAdmin || isAuthor) && editable;
        boolean canRecordProvostOpinion = (isAdmin || isStudentAffairs)
                && status.equals(RequestStatusName.FORMAL_EVALUATION.name());

        boolean underReview = status.equals(RequestStatusName.UNDER_REVIEW.name());
        Long actorDepartmentId = (actor != null && actor.getDepartment() != null)
                ? actor.getDepartment().getId() : null;
        boolean sameDepartment = actorDepartmentId != null
                && actorDepartmentId.equals(request.getDepartment().getId());

        Predicate<RequestFunding> canGrant = f -> underReview
                && fundingAuthorization.canGrantSource(roles,
                        FundingSourceName.valueOf(f.getSource().getName()), sameDepartment);

        return RequestResponse.of(request, canEdit, canDelete, canManageAttachments,
                canRecordProvostOpinion, canGrant);
    }
}
