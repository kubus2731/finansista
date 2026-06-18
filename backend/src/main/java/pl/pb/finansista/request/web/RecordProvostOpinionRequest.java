package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;

public record RecordProvostOpinionRequest(
        @NotBlank String opinion
) {
}
