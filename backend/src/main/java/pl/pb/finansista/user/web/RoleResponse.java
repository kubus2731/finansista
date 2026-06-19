package pl.pb.finansista.user.web;

import pl.pb.finansista.user.Role;

public record RoleResponse(Long id, String name) {
  public static RoleResponse of(Role role) {
    return new RoleResponse(role.getId(), role.getName());
  }
}
