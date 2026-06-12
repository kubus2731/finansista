package pl.pb.finansista.request.usecase;

import org.springframework.stereotype.Component;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.exception.UnauthorizedRequestAccessException;
import pl.pb.finansista.user.User;

import java.util.Collection;

@Component
public class RequestAccessValidator {

    public void validateUserCanAccessRequest(Request request, User user, Collection<String> roles) {
        boolean isAdmin = roles.contains("ROLE_ADMIN");
        if (isAdmin) return;

        boolean isAuthor = request.getUser().getId().equals(user.getId());
        if (isAuthor) return;

        boolean isDeanOffice = roles.contains("ROLE_DEAN_OFFICE");
        if (isDeanOffice) {
            if (request.getDepartment().getId().equals(user.getDepartment().getId())) {
                return;
            }
            throw UnauthorizedRequestAccessException.forAction("access this request outside your department");
        }

        boolean isFinanceOffice = roles.contains("ROLE_FINANCE_OFFICE");
        boolean isWrss = roles.contains("ROLE_WRSS");
        boolean isLegalCommission = roles.contains("ROLE_LEGAL_COMMISSION");

        if (isFinanceOffice || isWrss || isLegalCommission) {
            if (!request.getStatus().getName().equals("DRAFT")) {
                return;
            }
        }

        throw UnauthorizedRequestAccessException.forAction("access this request");
    }
}
