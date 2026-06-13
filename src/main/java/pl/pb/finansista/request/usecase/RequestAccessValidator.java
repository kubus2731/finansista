package pl.pb.finansista.request.usecase;

import org.springframework.stereotype.Component;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.User;
import pl.pb.finansista.reference.FundingSourceName;

import java.util.Collection;

@Component
public class RequestAccessValidator {

    public void validateUserCanAccessRequest(Request request, User user, Collection<String> roles) {
        boolean isAdmin = roles.contains(RoleName.ROLE_ADMIN.name());
        if (isAdmin) return;

        boolean isAuthor = request.getUser().getId().equals(user.getId());
        if (isAuthor) return;

        boolean isDeanOffice = roles.contains(RoleName.ROLE_DEAN_OFFICE.name());
        if (isDeanOffice) {
            if (request.getDepartment().getId().equals(user.getDepartment().getId())) {
                return;
            }
            throw UnauthorizedRequestAccessException.forAction("access this request outside your department");
        }

        boolean isFinanceOffice = roles.contains(RoleName.ROLE_FINANCE_OFFICE.name());
        boolean isStudentCouncil = roles.contains(RoleName.ROLE_STUDENT_COUNCIL.name());
        boolean isDoctoralCouncil = roles.contains(RoleName.ROLE_DOCTORAL_COUNCIL.name());
        boolean isLegalCommission = roles.contains(RoleName.ROLE_LEGAL_COMMISSION.name());

        if (isFinanceOffice) {
            if (!request.getStatus().getName().equals(RequestStatusName.DRAFT.name())) {
                return;
            }
        }

        String fundingSource = request.getFundingSource() != null ? request.getFundingSource().getName() : "";

        if (isLegalCommission) {
            if (!request.getStatus().getName().equals(RequestStatusName.DRAFT.name()) && 
                (fundingSource.equals(FundingSourceName.STUDENT_COUNCIL.name()) || fundingSource.equals(FundingSourceName.DOCTORAL_COUNCIL.name()))) {
                return;
            }
        }

        if (isStudentCouncil) {
            if (!request.getStatus().getName().equals(RequestStatusName.DRAFT.name()) && fundingSource.equals(FundingSourceName.STUDENT_COUNCIL.name())) {
                return;
            }
        }

        if (isDoctoralCouncil) {
            if (!request.getStatus().getName().equals(RequestStatusName.DRAFT.name()) && fundingSource.equals(FundingSourceName.DOCTORAL_COUNCIL.name())) {
                return;
            }
        }

        throw UnauthorizedRequestAccessException.forAction("access this request");
    }
}
