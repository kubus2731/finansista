package pl.pb.finansista.request.usecase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.FundingSourceName;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.RequestStatusName;
import pl.pb.finansista.user.RoleName;
import pl.pb.finansista.user.User;

@Component
public class RequestAccessSpecificationFactory {

  public Specification<Request> createForUser(User user, Collection<String> userAuthorities) {
    if (userAuthorities.contains(RoleName.ROLE_ADMIN.name())) {
      return (_, _, cb) -> cb.conjunction();
    }

    List<Specification<Request>> allowedSpecs = new ArrayList<>();

    allowedSpecs.add(RequestSpecifications.hasUserEmail(user.getEmail()));

        if (userAuthorities.contains(RoleName.ROLE_PROVOST.name())) {
            allowedSpecs.add(RequestSpecifications.hasStatusIn(List.of(
                    RequestStatusName.FORMAL_EVALUATION.name(), RequestStatusName.UNDER_REVIEW.name(),
                    RequestStatusName.ACCEPTED.name(), RequestStatusName.REJECTED.name(),
                    RequestStatusName.CORRECTION_REQUIRED.name())));
        }

        if (userAuthorities.contains(RoleName.ROLE_DEAN_OFFICE.name())) {
            Department deanDepartment = user.getDepartment();
            Long facultyId = (deanDepartment != null && deanDepartment.getParent() != null)
                    ? deanDepartment.getParent().getId() : null;
            if (facultyId != null) {
                allowedSpecs.add(RequestSpecifications.hasDepartment(facultyId)
                        .and(RequestSpecifications.hasFundingSource(FundingSourceName.FACULTY_FUNDS.name()))
                        .and(RequestSpecifications.hasStatusIn(List.of(
                                RequestStatusName.UNDER_REVIEW.name(), RequestStatusName.ACCEPTED.name(),
                                RequestStatusName.REJECTED.name(), RequestStatusName.CORRECTION_REQUIRED.name()))));
            }
        }

    if (userAuthorities.contains(RoleName.ROLE_DEAN_OFFICE.name())) {
      Department deanDepartment = user.getDepartment();
      Long facultyId =
          (deanDepartment != null && deanDepartment.getParent() != null)
              ? deanDepartment.getParent().getId()
              : null;
      if (facultyId != null) {
        allowedSpecs.add(
            RequestSpecifications.hasDepartment(facultyId)
                .and(RequestSpecifications.hasFundingSource(FundingSourceName.FACULTY_FUNDS.name()))
                .and(
                    RequestSpecifications.hasStatusIn(
                        List.of(
                            RequestStatusName.UNDER_REVIEW.name(),
                                RequestStatusName.ACCEPTED.name(),
                            RequestStatusName.REJECTED.name(),
                                RequestStatusName.CORRECTION_REQUIRED.name()))));
      }
    }

    if (userAuthorities.contains(RoleName.ROLE_LEGAL_COMMISSION.name())) {
      allowedSpecs.add(
          RequestSpecifications.hasStatusIn(
                  List.of(
                      RequestStatusName.FORMAL_EVALUATION.name(),
                      RequestStatusName.UNDER_REVIEW.name(),
                      RequestStatusName.ACCEPTED.name(),
                      RequestStatusName.REJECTED.name(),
                      RequestStatusName.CORRECTION_REQUIRED.name()))
              .and(
                  RequestSpecifications.hasFundingSource(FundingSourceName.STUDENT_COUNCIL.name())
                      .or(
                          RequestSpecifications.hasFundingSource(
                              FundingSourceName.DOCTORAL_COUNCIL.name()))));
    }

    if (userAuthorities.contains(RoleName.ROLE_STUDENT_COUNCIL.name())) {
      allowedSpecs.add(
          RequestSpecifications.hasStatusIn(
                  List.of(
                      RequestStatusName.FORMAL_EVALUATION.name(),
                      RequestStatusName.UNDER_REVIEW.name(),
                      RequestStatusName.ACCEPTED.name(),
                      RequestStatusName.REJECTED.name(),
                      RequestStatusName.CORRECTION_REQUIRED.name()))
              .and(
                  RequestSpecifications.hasFundingSource(
                      FundingSourceName.STUDENT_COUNCIL.name())));
    }

    if (userAuthorities.contains(RoleName.ROLE_DOCTORAL_COUNCIL.name())) {
      allowedSpecs.add(
          RequestSpecifications.hasStatusIn(
                  List.of(
                      RequestStatusName.FORMAL_EVALUATION.name(),
                      RequestStatusName.UNDER_REVIEW.name(),
                      RequestStatusName.ACCEPTED.name(),
                      RequestStatusName.REJECTED.name(),
                      RequestStatusName.CORRECTION_REQUIRED.name()))
              .and(
                  RequestSpecifications.hasFundingSource(
                      FundingSourceName.DOCTORAL_COUNCIL.name())));
    }

    if (userAuthorities.contains(RoleName.ROLE_FINANCE_OFFICE.name())) {
      allowedSpecs.add(
          RequestSpecifications.hasStatusIn(
              List.of(
                  RequestStatusName.UNDER_REVIEW.name(), RequestStatusName.ACCEPTED.name(),
                  RequestStatusName.REJECTED.name(),
                      RequestStatusName.CORRECTION_REQUIRED.name())));
    }

    return Specification.anyOf(allowedSpecs);
  }
}
