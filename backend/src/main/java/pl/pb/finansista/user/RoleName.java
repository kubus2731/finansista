package pl.pb.finansista.user;

public enum RoleName {
  ROLE_ADMIN,
  ROLE_STUDENT,
  ROLE_STUDENT_COUNCIL,
  ROLE_DOCTORAL_COUNCIL,
  ROLE_LEGAL_COMMISSION,
  ROLE_STUDENT_AFFAIRS,
  ROLE_PROVOST,
  ROLE_DEAN_OFFICE,
  ROLE_FINANCE_OFFICE;

  public boolean canReviewMerit() {
    return this == ROLE_STUDENT_COUNCIL
        || this == ROLE_DOCTORAL_COUNCIL
        || this == ROLE_LEGAL_COMMISSION;
  }

  public boolean isAdmin() {
    return this == ROLE_ADMIN;
  }
}
