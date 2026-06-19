package pl.pb.finansista.request.usecase;

import java.util.Collection;
import org.springframework.stereotype.Component;
import pl.pb.finansista.reference.FundingSourceName;
import pl.pb.finansista.user.RoleName;

/**
 * Maps each Section VI funding source to the dysponent allowed to sign its grant,
 * per the signature column of Załącznik 1 (Przewodniczący SSPB/SDPB, Rektor, Dziekan).
 */
@Component
public class RequestFundingAuthorization {

    public boolean canGrantSource(Collection<String> roles, FundingSourceName source, boolean isSameDepartment) {
        if (roles.contains(RoleName.ROLE_ADMIN.name())) {
            return true;
        }
        return switch (source) {
            case STUDENT_COUNCIL ->
                    roles.contains(RoleName.ROLE_STUDENT_COUNCIL.name()) || roles.contains(RoleName.ROLE_LEGAL_COMMISSION.name());
            case DOCTORAL_COUNCIL ->
                    roles.contains(RoleName.ROLE_DOCTORAL_COUNCIL.name()) || roles.contains(RoleName.ROLE_LEGAL_COMMISSION.name());
            case INITIATIVE_FUNDS -> roles.contains(RoleName.ROLE_FINANCE_OFFICE.name());
            case FACULTY_FUNDS -> roles.contains(RoleName.ROLE_DEAN_OFFICE.name()) && isSameDepartment;
        };
    }
}
