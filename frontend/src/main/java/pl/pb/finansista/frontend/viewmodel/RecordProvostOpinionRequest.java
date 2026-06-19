package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotBlank;

public record RecordProvostOpinionRequest(@NotBlank String opinion) {}
