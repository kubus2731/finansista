package pl.pb.finansista.request.usecase;

import pl.pb.finansista.request.SupervisorInfo;

public record SupervisorData(String name, String email, String phone, String department) {
  public static SupervisorInfo toDomainOrEmpty(SupervisorData data) {
    return data == null ? SupervisorInfo.empty() : data.toDomain();
  }

  public SupervisorInfo toDomain() {
    return new SupervisorInfo(name, email, phone, department);
  }
}
