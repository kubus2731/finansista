package pl.pb.finansista.frontend.exception;
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
    public org.springframework.http.HttpStatus getHttpStatus() { return org.springframework.http.HttpStatus.BAD_REQUEST; }
}
