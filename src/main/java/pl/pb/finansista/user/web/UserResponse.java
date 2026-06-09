package pl.pb.finansista.user.web;

import pl.pb.finansista.user.User;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String surname,
        String email,
        String phoneNumber,
        String roleName,
        String departmentName
) {
    public static UserResponse of(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().getName(),
                user.getDepartment().getName()
        );
    }
}