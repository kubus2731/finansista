package pl.pb.finansista.request.usecase;

import pl.pb.finansista.request.ProjectDetails;

import java.time.LocalDate;

public record ProjectDetailsData(
        String realizerType,
        String projectKind,
        String projectKindOther,
        String projectScope,
        String projectScopeOther,
        String projectNature,
        String projectNatureOther,
        LocalDate plannedDateFrom,
        LocalDate plannedDateTo,
        String location,
        Integer participantsInvolved,
        Integer participantsBenefiting
) {
    public ProjectDetails toDomain() {
        return new ProjectDetails(realizerType, projectKind, projectKindOther, projectScope,
                projectScopeOther, projectNature, projectNatureOther, plannedDateFrom,
                plannedDateTo, location, participantsInvolved, participantsBenefiting);
    }

    public static ProjectDetails toDomainOrEmpty(ProjectDetailsData data) {
        return data == null ? ProjectDetails.empty() : data.toDomain();
    }
}
