package pl.pb.finansista.request.usecase;

import pl.pb.finansista.request.SupervisorInfo;

/** Use-case input mirror of {@link SupervisorInfo} (Załącznik 1, sekcja II). */
public record SupervisorData(
        String name,
        String email,
        String phone,
        String department
) {
    public SupervisorInfo toDomain() {
        return new SupervisorInfo(name, email, phone, department);
    }
}
