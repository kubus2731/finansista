package pl.pb.finansista.request.usecase;

import org.springframework.data.jpa.domain.Specification;
import pl.pb.finansista.request.Request;

import java.util.List;
import java.util.UUID;


public class RequestSpecifications {

    public static Specification<Request> hasExternalId(UUID externalId) {
        return (root, query, cb) ->
                externalId == null ? null : cb.equal(root.get("externalId"), externalId);
    }

    public static Specification<Request> hasDepartment(Long departmentId) {
        return (root, query, cb) ->
                departmentId == null ? null : cb.equal(root.join("department").get("id"), departmentId);
    }

    public static Specification<Request> hasStatus(String statusName) {
        return (root, query, cb) ->
                statusName == null ? null : cb.equal(root.join("status").get("name"), statusName);
    }

    public static Specification<Request> hasStatusIn(List<String> statuses) {
        return (root, query, cb) ->
                (statuses == null || statuses.isEmpty()) ? null : root.join("status").get("name").in(statuses);
    }

    public static Specification<Request> hasUserEmail(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.equal(root.join("user").get("email"), email);
    }

    public static Specification<Request> hasFundingSource(String fundingSourceCode) {
        return (root, query, cb) ->
                fundingSourceCode == null ? null : cb.equal(root.join("fundingSource").get("name"), fundingSourceCode);
    }

    public static Specification<Request> containsText(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }
}
