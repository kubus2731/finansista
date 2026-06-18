package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotNull;


import java.util.UUID;

public record ChangeUserActiveRequest(
        @NotNull Boolean active
) {
    
}

