package pl.pb.finansista.user.web;

import pl.pb.finansista.user.User;
import pl.pb.finansista.common.web.ExternalIdEncoder;

public record UserResponse(
        String id,
        String name,
        String surname,
        String email,
        String phoneNumber,
        String roleName,
        String departmentName
) {
    public static UserResponse of(User user) {
        return new UserResponse(
                ExternalIdEncoder.encode("usr", user.getExternalId()),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().getName(),
                user.getDepartment().getName()
        );
    }
}