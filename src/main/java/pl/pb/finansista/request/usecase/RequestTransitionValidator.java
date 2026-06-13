package pl.pb.finansista.request.usecase;

import org.springframework.stereotype.Component;
import pl.pb.finansista.reference.FundingSourceName;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class RequestTransitionValidator {

    public List<RequestStatusName> getAvailableTransitions(Request request, User actor, Collection<String> roles) {
        RequestStatusName oldStatus = RequestStatusName.valueOf(request.getStatus().getName());
        boolean isAdmin = roles.contains(RoleName.ROLE_ADMIN.name());

        List<RequestStatusName> available = new ArrayList<>();
        String fundingSource = request.getFundingSource() != null ? request.getFundingSource().getName() : "";
        boolean isAuthor = request.getUser().getId().equals(actor.getId());
        boolean isSameDepartment = request.getDepartment().getId().equals(actor.getDepartment().getId());

        switch (oldStatus) {
            case DRAFT -> {
                if (isAdmin || isAuthor) {
                    available.add(RequestStatusName.SUBMITTED);
                }
            }
            case SUBMITTED -> {
                if (isAdmin || (roles.contains(RoleName.ROLE_DEAN_OFFICE.name()) && isSameDepartment)) {
                    available.add(RequestStatusName.FORMAL_EVALUATION);
                    available.add(RequestStatusName.REJECTED);
                    available.add(RequestStatusName.CORRECTION_REQUIRED);
                }
            }
            case FORMAL_EVALUATION -> {
                boolean isDean = roles.contains(RoleName.ROLE_DEAN_OFFICE.name()) && isSameDepartment;
                if (isAdmin || canEvaluate(roles, fundingSource, isSameDepartment)) {
                    available.add(RequestStatusName.UNDER_REVIEW);
                }
                
                if (isAdmin || isDean || canEvaluate(roles, fundingSource, isSameDepartment)) {
                    available.add(RequestStatusName.REJECTED);
                    available.add(RequestStatusName.CORRECTION_REQUIRED);
                }
            }
            case UNDER_REVIEW -> {
                if (isAdmin || canAccept(roles, fundingSource, isSameDepartment)) {
                    available.add(RequestStatusName.ACCEPTED);
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
            default -> {}
        }
        return available;
    }

    private static boolean canEvaluate(Collection<String> roles, String fundingSource, boolean isSameDepartment) {
        if (fundingSource.equals(FundingSourceName.FACULTY_FUNDS.name()) || fundingSource.equals(FundingSourceName.INITIATIVE_FUNDS.name())) {
            return roles.contains(RoleName.ROLE_DEAN_OFFICE.name()) && isSameDepartment;
        } else if (fundingSource.equals(FundingSourceName.STUDENT_COUNCIL.name())) {
            return roles.contains(RoleName.ROLE_STUDENT_COUNCIL.name()) || roles.contains(RoleName.ROLE_LEGAL_COMMISSION.name());
        } else if (fundingSource.equals(FundingSourceName.DOCTORAL_COUNCIL.name())) {
            return roles.contains(RoleName.ROLE_DOCTORAL_COUNCIL.name()) || roles.contains(RoleName.ROLE_LEGAL_COMMISSION.name());
        }

        return false;
    }

    private static boolean canAccept(Collection<String> roles, String fundingSource, boolean isSameDepartment) {
        if (fundingSource.equals(FundingSourceName.FACULTY_FUNDS.name())) {
            return roles.contains(RoleName.ROLE_FINANCE_OFFICE.name()) || (roles.contains(RoleName.ROLE_DEAN_OFFICE.name()) && isSameDepartment);
        }

        return roles.contains(RoleName.ROLE_FINANCE_OFFICE.name());
    }


    public void validateTransition(Request request, User actor, Collection<String> roles, RequestStatusName targetStatus) {
        List<RequestStatusName> available = getAvailableTransitions(request, actor, roles);
        if (!available.contains(targetStatus)) {
            throw UnauthorizedRequestAccessException.forAction("perform transition to " + targetStatus.name());
        }
    }
}
