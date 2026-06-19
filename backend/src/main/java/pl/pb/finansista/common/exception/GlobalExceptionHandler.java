package pl.pb.finansista.common.exception;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatusCode statusCode, @NonNull WebRequest request) {

        var result = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (result != null && result.getBody() instanceof ProblemDetail pd) {
            pd.setProperty("code", codeFor(ex));
            pd.setProperty("traceId", MDC.get("traceId"));
        }

        return result;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        var result = super.handleMethodArgumentNotValid(ex, headers, status, request);
        if (result != null && result.getBody() instanceof ProblemDetail pd) {
            List<Map<String, String>> errors = ex.getBindingResult().getAllErrors().stream()
                    .map(err -> Map.of(
                            "field", err instanceof FieldError fe ? fe.getField() : err.getObjectName(),
                            "message", Objects.requireNonNullElse(err.getDefaultMessage(), "invalid")))
                    .toList();
            pd.setProperty("errors", errors);
        }

        return result;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) {
        log.error("An unexpected error occurred", ex);

        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.");
        pd.setTitle("Internal Server Error");
        pd.setProperty("code", codeFor(ex));
        pd.setProperty("traceId", getTraceId());

        return ResponseEntity.internalServerError().body(pd);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex) {
        log.warn("Business rule violation occurred: {}", ex.getMessage());

        var pd = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
        pd.setTitle("Business Rule Violation");
        pd.setProperty("code", codeFor(ex));
        pd.setProperty("traceId", getTraceId());

        return ResponseEntity.status(ex.getHttpStatus()).body(pd);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(Exception ex) {
        log.warn("Access denied: {}", ex.getMessage());

        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.");
        pd.setTitle("Forbidden");
        pd.setProperty("code", codeFor(ex));
        pd.setProperty("traceId", getTraceId());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(pd);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        log.warn("Concurrency conflict occurred: {}", ex.getMessage());

        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "The data has been modified by another user in the meantime. Please refresh the page and try again.");
        pd.setTitle("Concurrency Conflict");
        pd.setProperty("code", codeFor(ex));
        pd.setProperty("traceId", getTraceId());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());

        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "This operation conflicts with existing data (the record may still be in use or a value must be unique).");
        pd.setTitle("Data Integrity Conflict");
        pd.setProperty("code", codeFor(ex));
        pd.setProperty("traceId", getTraceId());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("Upload exceeded the maximum allowed size: {}", ex.getMessage());

        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE,
                "The uploaded file exceeds the maximum allowed size.");
        pd.setTitle("Payload Too Large");

        return handleExceptionInternal(ex, pd, headers, HttpStatus.PAYLOAD_TOO_LARGE, request);
    }

    private String codeFor(Exception ex) {
        return ex.getClass().getSimpleName();
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null ? traceId : "N/A";
    }
}
