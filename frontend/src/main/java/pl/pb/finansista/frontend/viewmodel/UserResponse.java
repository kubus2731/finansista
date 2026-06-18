package pl.pb.finansista.frontend.viewmodel;

public record UserResponse(
        String id,
        String name,
        String surname,
        String email,
        String phoneNumber,
        String roleName,
        Long departmentId,
        String departmentName,
        boolean active
) {
    
}
