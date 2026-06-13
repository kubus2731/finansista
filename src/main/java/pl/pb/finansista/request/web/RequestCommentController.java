package pl.pb.finansista.request.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pl.pb.finansista.request.Comment;
import pl.pb.finansista.request.usecase.AddCommentUseCase;
import pl.pb.finansista.request.usecase.GetCommentsUseCase;
import pl.pb.finansista.request.usecase.GetSingleRequestQuery;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestCommentController {

    private final AddCommentUseCase addCommentUseCase;
    private final GetCommentsUseCase getCommentsUseCase;

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID id,
            @Valid @RequestBody AddCommentRequest payload,
            Authentication authentication)
    {
        log.info("Adding comment to request ID: {} by user: {}", id, authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Comment comment = addCommentUseCase.execute(payload.toCommand(id, authentication.getName(), authorities));
        log.info("Successfully added comment with ID: {}", comment.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.of(comment));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable UUID id,
            Authentication authentication)
    {
        log.info("Fetching comments for request ID: {} by user: {}", id, authentication.getName());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), authorities);

        List<CommentResponse> comments = getCommentsUseCase.execute(query).stream()
                .map(CommentResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);
    }
}