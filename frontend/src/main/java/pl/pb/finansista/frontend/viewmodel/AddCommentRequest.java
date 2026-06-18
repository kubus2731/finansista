package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record AddCommentRequest(
        @NotBlank String content
) {
    
}

