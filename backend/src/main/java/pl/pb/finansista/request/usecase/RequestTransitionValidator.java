package pl.pb.finansista.request.usecase;

import org.springframework.stereotype.Component;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Request-level status pipeline. Per-source granting is NOT a transition — it
 * happens via {@link GrantFundingUseCase} while UNDER_REVIEW. The request can
 * only reach ACCEPTED once every funding row has been signed by its dysponent.
 *
 * <ul>
 *   <li>SUBMITTED → FORMAL_EVALUATION: CSSDiR (ROLE_STUDENT_AFFAIRS) — ocena formalna</li>
 *   <li>FORMAL_EVALUATION → UNDER_REVIEW: CSSDiR, gated on the recorded opinia prorektora (merit)</li>
 *   <li>UNDER_REVIEW → ACCEPTED: Rektor (ROLE_FINANCE_OFFICE), gated on all rows granted</li>
 * </ul>
 */
@Component
public class RequestTransitionValidator {

    public List<RequestStatusName> getAvailableTransitions(Request request, User actor, Collection<String> roles) {
        RequestStatusName oldStatus = RequestStatusName.valueOf(request.getStatus().getName());

        boolean isAdmin = roles.contains(RoleName.ROLE_ADMIN.name());
        boolean isAuthor = request.getUser().getId().equals(actor.getId());
        boolean isStudentAffairs = roles.contains(RoleName.ROLE_STUDENT_AFFAIRS.name());
        boolean isFinance = roles.contains(RoleName.ROLE_FINANCE_OFFICE.name());

        List<RequestStatusName> available = new ArrayList<>();

        switch (oldStatus) {
            case DRAFT -> {
                if (isAdmin || isAuthor) {
                    available.add(RequestStatusName.SUBMITTED);
                }
            }
            case SUBMITTED -> {
                if (isAdmin || isStudentAffairs) {
                    available.add(RequestStatusName.FORMAL_EVALUATION);
                    available.add(RequestStatusName.REJECTED);
                    available.add(RequestStatusName.CORRECTION_REQUIRED);
                }
            }
            case FORMAL_EVALUATION -> {
                if (isAdmin || isStudentAffairs) {
                    if (request.hasProvostOpinion()) {
                        available.add(RequestStatusName.UNDER_REVIEW);
                    }
                    available.add(RequestStatusName.REJECTED);
                    available.add(RequestStatusName.CORRECTION_REQUIRED);
                }
            }
            case UNDER_REVIEW -> {
                if (isAdmin || isFinance) {
                    if (request.allFundingGranted()) {
                        available.add(RequestStatusName.ACCEPTED);
                    }
                    available.add(RequestStatusName.REJECTED);
                    available.add(RequestStatusName.CORRECTION_REQUIRED);
                }
            }
            case CORRECTION_REQUIRED -> {
                if (isAdmin || isAuthor) {
                    available.add(RequestStatusName.DRAFT);
                    available.add(RequestStatusName.SUBMITTED);
                }
            }
            default -> {
            }
        }
        return available;
    }

    public void validateTransition(Request request, User actor, Collection<String> roles, RequestStatusName targetStatus) {
        List<RequestStatusName> available = getAvailableTransitions(request, actor, roles);
        if (!available.contains(targetStatus)) {
            throw UnauthorizedRequestAccessException.forAction("perform transition to " + targetStatus.name());
        }
    }
}
