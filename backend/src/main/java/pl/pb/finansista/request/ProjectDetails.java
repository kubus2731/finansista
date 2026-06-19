package pl.pb.finansista.request;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** Załącznik 1, sekcja I: dane przedsięwzięcia. */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectDetails {

  private String realizerType;
  private String projectKind;
  private String projectKindOther;
  private String projectScope;
  private String projectScopeOther;
  private String projectNature;
  private String projectNatureOther;
  private LocalDate plannedDateFrom;
  private LocalDate plannedDateTo;
  private String location;
  private Integer participantsInvolved;
  private Integer participantsBenefiting;

  public ProjectDetails(
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
      Integer participantsBenefiting) {
    this.realizerType = realizerType;
    this.projectKind = projectKind;
    this.projectKindOther = projectKindOther;
    this.projectScope = projectScope;
    this.projectScopeOther = projectScopeOther;
    this.projectNature = projectNature;
    this.projectNatureOther = projectNatureOther;
    this.plannedDateFrom = plannedDateFrom;
    this.plannedDateTo = plannedDateTo;
    this.location = location;
    this.participantsInvolved = participantsInvolved;
    this.participantsBenefiting = participantsBenefiting;
  }

  public static ProjectDetails empty() {
    return new ProjectDetails(
        null, null, null, null, null, null, null, null, null, null, null, null);
  }
}
