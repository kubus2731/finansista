package pl.pb.finansista.request.usecase;

import org.springframework.data.jpa.domain.Specification;
import pl.pb.finansista.request.Request;

import java.util.UUID;

public class RequestSpecifications {

    public static Specification<Request> hasDepartment(UUID departmentId) {
        return (root, query, cb) -> 
                departmentId == null ? null : cb.equal(root.join("department").get("id"), departmentId);
    }

    public static Specification<Request> hasStatus(String statusName) {
        return (root, query, cb) -> 
                statusName == null ? null : cb.equal(root.join("status").get("name"), statusName);
    }

    public static Specification<Request> hasUserEmail(String email) {
        return (root, query, cb) -> 
                email == null ? null : cb.equal(root.join("user").get("email"), email);
    }
}
